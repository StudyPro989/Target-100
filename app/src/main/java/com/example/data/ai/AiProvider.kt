package com.example.data.ai

import com.example.model.AiProviderType
import com.example.model.Difficulty
import com.example.model.QuestionModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

interface AiProviderAdapter {
    val providerType: AiProviderType
    suspend fun testConnection(apiKey: String, model: String, endpoint: String? = null): ConnectionTestResult
    suspend fun generateChapterTest(
        apiKey: String,
        model: String,
        endpoint: String? = null,
        subjectName: String,
        chapterTitle: String,
        questionCount: Int,
        difficulty: Difficulty
    ): Result<List<QuestionModel>>
}

data class ConnectionTestResult(
    val success: Boolean,
    val message: String
)

object JsonQuestionParser {
    fun parseAndValidateQuestions(
        rawJsonOrText: String,
        expectedCount: Int
    ): Result<List<QuestionModel>> {
        try {
            // Find JSON array within string if wrapped in markdown
            val cleaned = cleanJsonString(rawJsonOrText)
            val jsonArray = if (cleaned.startsWith("[")) {
                JSONArray(cleaned)
            } else if (cleaned.startsWith("{")) {
                val obj = JSONObject(cleaned)
                obj.optJSONArray("questions") ?: JSONArray()
            } else {
                return Result.failure(IllegalArgumentException("Response does not contain valid JSON array"))
            }

            val resultList = mutableListOf<QuestionModel>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val id = item.optInt("id", i + 1)
                val question = item.optString("question", "").trim()
                if (question.isEmpty()) continue

                val optionsArray = item.getJSONArray("options")
                if (optionsArray.length() != 4) {
                    continue // Strictly 4 options required by spec
                }

                val optionsList = mutableListOf<String>()
                for (optIdx in 0 until 4) {
                    val opt = optionsArray.getString(optIdx).trim()
                    if (opt.isNotEmpty()) {
                        optionsList.add(opt)
                    }
                }
                if (optionsList.size != 4) continue

                // Check correct index (may come as integer 0-3 or "A", "B", "C", "D")
                var correctIndex = 0
                if (item.has("correctIndex")) {
                    correctIndex = item.getInt("correctIndex")
                } else if (item.has("correctAnswerIndex")) {
                    correctIndex = item.getInt("correctAnswerIndex")
                } else if (item.has("correctAnswer")) {
                    val ansStr = item.getString("correctAnswer").trim().uppercase()
                    correctIndex = when (ansStr) {
                        "A", "1" -> 0
                        "B", "2" -> 1
                        "C", "3" -> 2
                        "D", "4" -> 3
                        else -> {
                            val idx = optionsList.indexOfFirst { it.equals(ansStr, ignoreCase = true) }
                            if (idx >= 0) idx else 0
                        }
                    }
                }

                if (correctIndex !in 0..3) {
                    correctIndex = 0
                }

                val explanation = item.optString("explanation", "Prescribed RBSE syllabus concept.").trim()

                resultList.add(
                    QuestionModel(
                        id = id,
                        question = question,
                        options = optionsList,
                        correctIndex = correctIndex,
                        explanation = if (explanation.isEmpty()) "Concept from RBSE Class 10 syllabus." else explanation
                    )
                )
            }

            if (resultList.isEmpty()) {
                return Result.failure(IllegalStateException("No valid multiple choice questions with 4 options could be parsed."))
            }

            // Truncate to expectedCount if returned more, as specified in rules
            val finalList = if (resultList.size > expectedCount) {
                resultList.take(expectedCount)
            } else {
                resultList
            }

            return Result.success(finalList)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun cleanJsonString(input: String): String {
        var str = input.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json")
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```")
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```")
        }
        val firstBracket = str.indexOf('[')
        val firstBrace = str.indexOf('{')
        val startIndex = if (firstBracket >= 0 && (firstBrace < 0 || firstBracket < firstBrace)) {
            firstBracket
        } else if (firstBrace >= 0) {
            firstBrace
        } else {
            0
        }
        val lastBracket = str.lastIndexOf(']')
        val lastBrace = str.lastIndexOf('}')
        val endIndex = maxOf(lastBracket, lastBrace)
        if (startIndex in 0 until endIndex) {
            return str.substring(startIndex, endIndex + 1).trim()
        }
        return str.trim()
    }
}

class GeminiProviderAdapter(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) : AiProviderAdapter {

    override val providerType: AiProviderType = AiProviderType.GEMINI

    override suspend fun testConnection(apiKey: String, model: String, endpoint: String?): ConnectionTestResult {
        return try {
            val sanitizedKey = apiKey.trim()
            if (sanitizedKey.isEmpty()) {
                return ConnectionTestResult(false, "API Key is empty. Please enter your Gemini API key.")
            }
            val targetModel = if (model.isBlank()) "gemini-3.5-flash" else model.trim()
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$targetModel:generateContent?key=$sanitizedKey"

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", "Ping test. Respond with 'OK'.") })
                        })
                    })
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                ConnectionTestResult(true, "Successfully connected to Google Gemini ($targetModel)!")
            } else {
                val code = response.code
                val errBody = response.body?.string() ?: ""
                val msg = when (code) {
                    400 -> "Invalid request or key format (HTTP 400)."
                    403 -> "Invalid or restricted API key (HTTP 403)."
                    429 -> "Rate limit exceeded (HTTP 429). Please try later."
                    else -> "Connection failed (HTTP $code)."
                }
                ConnectionTestResult(false, msg)
            }
        } catch (e: Exception) {
            ConnectionTestResult(false, "Network error: ${e.message}")
        }
    }

    override suspend fun generateChapterTest(
        apiKey: String,
        model: String,
        endpoint: String?,
        subjectName: String,
        chapterTitle: String,
        questionCount: Int,
        difficulty: Difficulty
    ): Result<List<QuestionModel>> {
        return try {
            val sanitizedKey = apiKey.trim()
            if (sanitizedKey.isEmpty()) {
                return Result.failure(IllegalStateException("Gemini API key is not configured."))
            }

            val targetModel = if (model.isBlank()) "gemini-3.5-flash" else model.trim()
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$targetModel:generateContent?key=$sanitizedKey"

            val prompt = """
You are an expert question maker for RBSE (Rajasthan Board of Secondary Education) Class 10.
Generate exactly $questionCount multiple-choice questions (MCQs) for the subject "$subjectName" and chapter "$chapterTitle".
Difficulty level: ${difficulty.name}.
Target audience: RBSE Class 10 students.

Format: Return ONLY a valid JSON array of $questionCount objects matching this exact schema:
[
  {
    "id": 1,
    "question": "Clear question text?",
    "options": ["Option A", "Option B", "Option C", "Option D"],
    "correctIndex": 0,
    "explanation": "Concise explanation of the correct answer."
  }
]

CRITICAL RULES:
- Exactly 4 options in the options array.
- correctIndex must be an integer from 0 to 3 indicating the index in options.
- No markdown formatting outside the JSON array.
- Strictly adhere to RBSE Class 10 curriculum standards.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val err = when (code) {
                    403 -> "Gemini API key is invalid or unauthorized (HTTP 403)."
                    429 -> "Gemini API rate limit reached (HTTP 429). Please wait before retrying."
                    else -> "Gemini API error (HTTP $code): ${bodyString.take(150)}"
                }
                return Result.failure(Exception(err))
            }

            val rootJson = JSONObject(bodyString)
            val candidates = rootJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return Result.failure(Exception("Gemini returned empty candidates."))
            }
            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.getJSONObject(0)?.optString("text") ?: ""

            if (text.isBlank()) {
                return Result.failure(Exception("Gemini returned empty text response."))
            }

            JsonQuestionParser.parseAndValidateQuestions(text, questionCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class OpenAiCompatibleProviderAdapter(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) : AiProviderAdapter {

    override val providerType: AiProviderType = AiProviderType.OPENAI_COMPATIBLE

    override suspend fun testConnection(apiKey: String, model: String, endpoint: String?): ConnectionTestResult {
        return try {
            val baseEndpoint = endpoint?.trim().takeUnless { it.isNullOrEmpty() } ?: "https://api.openai.com/v1"
            val targetUrl = if (baseEndpoint.endsWith("/chat/completions")) baseEndpoint else "$baseEndpoint/chat/completions"
            val targetModel = if (model.isBlank()) "gpt-4o-mini" else model.trim()

            val jsonBody = JSONObject().apply {
                put("model", targetModel)
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", "Ping test. Respond with OK.")
                    })
                }
                put("messages", messages)
                put("max_tokens", 5)
            }

            val reqBuilder = Request.Builder()
                .url(targetUrl)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            if (apiKey.isNotBlank()) {
                reqBuilder.addHeader("Authorization", "Bearer ${apiKey.trim()}")
            }

            val response = client.newCall(reqBuilder.build()).execute()
            if (response.isSuccessful) {
                ConnectionTestResult(true, "Successfully connected to OpenAI-compatible endpoint!")
            } else {
                ConnectionTestResult(false, "Connection error: HTTP ${response.code}")
            }
        } catch (e: Exception) {
            ConnectionTestResult(false, "Endpoint network error: ${e.message}")
        }
    }

    override suspend fun generateChapterTest(
        apiKey: String,
        model: String,
        endpoint: String?,
        subjectName: String,
        chapterTitle: String,
        questionCount: Int,
        difficulty: Difficulty
    ): Result<List<QuestionModel>> {
        return try {
            val baseEndpoint = endpoint?.trim().takeUnless { it.isNullOrEmpty() } ?: "https://api.openai.com/v1"
            val targetUrl = if (baseEndpoint.endsWith("/chat/completions")) baseEndpoint else "$baseEndpoint/chat/completions"
            val targetModel = if (model.isBlank()) "gpt-4o-mini" else model.trim()

            val prompt = """
You are an expert question maker for RBSE Class 10.
Generate exactly $questionCount MCQs for subject "$subjectName" and chapter "$chapterTitle" at ${difficulty.name} difficulty.
Return ONLY a valid JSON array of objects:
[
  {
    "id": 1,
    "question": "Question text?",
    "options": ["A", "B", "C", "D"],
    "correctIndex": 0,
    "explanation": "Reason"
  }
]
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                put("model", targetModel)
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are an RBSE Class 10 exam generator that only outputs JSON arrays of 4-option MCQs.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }
                put("messages", messages)
                put("temperature", 0.3)
            }

            val reqBuilder = Request.Builder()
                .url(targetUrl)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            if (apiKey.isNotBlank()) {
                reqBuilder.addHeader("Authorization", "Bearer ${apiKey.trim()}")
            }

            val response = client.newCall(reqBuilder.build()).execute()
            if (!response.isSuccessful) {
                return Result.failure(Exception("Provider returned HTTP ${response.code}"))
            }

            val rootJson = JSONObject(response.body?.string() ?: "")
            val choices = rootJson.optJSONArray("choices")
            val firstChoice = choices?.getJSONObject(0)
            val messageObj = firstChoice?.getJSONObject("message")
            val content = messageObj?.optString("content", "") ?: ""

            JsonQuestionParser.parseAndValidateQuestions(content, questionCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
