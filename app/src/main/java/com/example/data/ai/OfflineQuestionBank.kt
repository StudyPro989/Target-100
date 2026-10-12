package com.example.data.ai

import com.example.model.Difficulty
import com.example.model.QuestionModel

object OfflineQuestionBank {

    // Curated high-yield RBSE Class 10 Board exam questions for core subjects & chapters
    private val BANK = mapOf(
        // Mathematics - Real Numbers
        101L to listOf(
            QuestionModel(
                id = 1,
                question = "What is the HCF of 96 and 404 by prime factorisation method?",
                options = listOf("4", "2", "8", "16"),
                correctIndex = 0,
                explanation = "96 = 2^5 * 3, 404 = 2^2 * 101. HCF = 2^2 = 4."
            ),
            QuestionModel(
                id = 2,
                question = "According to Euclid's division lemma, for given positive integers a and b, there exist unique integers q and r satisfying a = bq + r, where:",
                options = listOf("0 <= r < b", "0 < r <= b", "0 <= r <= b", "0 < r < b"),
                correctIndex = 0,
                explanation = "The remainder r satisfies the strict condition 0 <= r < b."
            ),
            QuestionModel(
                id = 3,
                question = "The decimal expansion of 13 / 3125 is:",
                options = listOf("Terminating", "Non-terminating repeating", "Non-terminating non-repeating", "None of these"),
                correctIndex = 0,
                explanation = "3125 = 5^5 = 2^0 * 5^5. The denominator is of form 2^m * 5^n, hence it terminates."
            ),
            QuestionModel(
                id = 4,
                question = "Which of the following numbers is an irrational number?",
                options = listOf("√2", "√4", "√9", "√16"),
                correctIndex = 0,
                explanation = "√2 is a non-terminating, non-recurring decimal, making it irrational."
            ),
            QuestionModel(
                id = 5,
                question = "If HCF(a, b) = 12 and a * b = 1800, then LCM(a, b) is:",
                options = listOf("150", "90", "120", "200"),
                correctIndex = 0,
                explanation = "HCF * LCM = Product of numbers. 12 * LCM = 1800 => LCM = 1800 / 12 = 150."
            )
        ),
        // Mathematics - Polynomials
        102L to listOf(
            QuestionModel(
                id = 1,
                question = "If α and β are the zeroes of the quadratic polynomial ax² + bx + c, then α + β equals:",
                options = listOf("-b/a", "c/a", "b/a", "-c/a"),
                correctIndex = 0,
                explanation = "Sum of zeroes of ax² + bx + c is -b/a."
            ),
            QuestionModel(
                id = 2,
                question = "The number of zeroes that a polynomial of degree n can have at most is:",
                options = listOf("n", "n - 1", "n + 1", "2n"),
                correctIndex = 0,
                explanation = "A polynomial of degree n has at most n real zeroes."
            ),
            QuestionModel(
                id = 3,
                question = "What is the product of zeroes of x² - 5x + 6?",
                options = listOf("6", "-6", "5", "-5"),
                correctIndex = 0,
                explanation = "Product of zeroes = c/a = 6/1 = 6."
            ),
            QuestionModel(
                id = 4,
                question = "A quadratic polynomial whose zeroes are -3 and 4 is:",
                options = listOf("x² - x - 12", "x² + x - 12", "x² - 7x + 12", "x² + 7x - 12"),
                correctIndex = 0,
                explanation = "P(x) = x² - (sum)x + product = x² - (-3+4)x + (-3*4) = x² - x - 12."
            ),
            QuestionModel(
                id = 5,
                question = "The graph of y = p(x) intersects the X-axis at 3 points. How many zeroes does p(x) have?",
                options = listOf("3", "2", "1", "0"),
                correctIndex = 0,
                explanation = "The number of zeroes is equal to the number of points where the graph intersects the X-axis."
            )
        ),
        // Science - Chemical Reactions and Equations
        201L to listOf(
            QuestionModel(
                id = 1,
                question = "When magnesium ribbon burns in air with a dazzling white flame, the product formed is:",
                options = listOf("Magnesium oxide (MgO)", "Magnesium hydroxide", "Magnesium carbonate", "Magnesium chloride"),
                correctIndex = 0,
                explanation = "2Mg + O₂ -> 2MgO (Magnesium Oxide white powder)."
            ),
            QuestionModel(
                id = 2,
                question = "The reaction in which heat is released along with the formation of products is called:",
                options = listOf("Exothermic reaction", "Endothermic reaction", "Combination reaction", "Decomposition reaction"),
                correctIndex = 0,
                explanation = "Exothermic reactions give out heat energy."
            ),
            QuestionModel(
                id = 3,
                question = "Pb(NO₃)₂ on heating undergoes decomposition to produce a brown gas. This gas is:",
                options = listOf("NO₂", "N₂O", "NO", "N₂O₅"),
                correctIndex = 0,
                explanation = "2Pb(NO₃)₂ -> 2PbO + 4NO₂ (brown gas) + O₂."
            ),
            QuestionModel(
                id = 4,
                question = "Rusting of iron is an example of:",
                options = listOf("Corrosion and Oxidation", "Reduction only", "Thermal decomposition", "Displacement only"),
                correctIndex = 0,
                explanation = "Iron is oxidized to hydrated iron oxide in the presence of oxygen and moisture."
            ),
            QuestionModel(
                id = 5,
                question = "Which gas is flushed into potato chip packets to prevent rancidity?",
                options = listOf("Nitrogen", "Oxygen", "Carbon dioxide", "Hydrogen"),
                correctIndex = 0,
                explanation = "Nitrogen is an unreactive inert gas that prevents oxidation of fats/oils."
            )
        ),
        // Science - Electricity
        211L to listOf(
            QuestionModel(
                id = 1,
                question = "The SI unit of electric potential difference is:",
                options = listOf("Volt", "Ampere", "Ohm", "Watt"),
                correctIndex = 0,
                explanation = "Potential difference is measured in Volts (V), named after Alessandro Volta."
            ),
            QuestionModel(
                id = 2,
                question = "Ohm's law gives the relationship between potential difference V and current I as:",
                options = listOf("V = I * R", "V = I / R", "I = V * R", "R = V * I"),
                correctIndex = 0,
                explanation = "V = I * R, where R is the resistance of the conductor."
            ),
            QuestionModel(
                id = 3,
                question = "Three resistors of 2 Ω, 3 Ω, and 6 Ω are connected in parallel. Their equivalent resistance is:",
                options = listOf("1 Ω", "11 Ω", "2 Ω", "0.5 Ω"),
                correctIndex = 0,
                explanation = "1/R = 1/2 + 1/3 + 1/6 = 3/6 + 2/6 + 1/6 = 6/6 = 1 => R = 1 Ω."
            ),
            QuestionModel(
                id = 4,
                question = "Commercial unit of electrical energy (1 Unit) is equal to:",
                options = listOf("3.6 * 10⁶ Joules (1 kWh)", "1000 Joules", "3600 Joules", "10⁵ Joules"),
                correctIndex = 0,
                explanation = "1 kWh = 1000 W * 3600 s = 3.6 * 10⁶ J."
            ),
            QuestionModel(
                id = 5,
                question = "An ammeter is always connected in ________ in a circuit:",
                options = listOf("Series", "Parallel", "Any manner", "Bridge circuit"),
                correctIndex = 0,
                explanation = "Ammeters have very low resistance and are connected in series to measure total current."
            )
        )
    )

    fun getQuestions(
        chapterId: Long,
        chapterTitle: String,
        subjectName: String,
        count: Int,
        difficulty: Difficulty
    ): List<QuestionModel> {
        val specific = BANK[chapterId]
        val questions = if (!specific.isNullOrEmpty()) {
            specific.toMutableList()
        } else {
            generateDynamicTemplateQuestions(chapterId, chapterTitle, subjectName, difficulty)
        }

        // Return up to count requested
        return questions.take(count)
    }

    private fun generateDynamicTemplateQuestions(
        chapterId: Long,
        chapterTitle: String,
        subjectName: String,
        difficulty: Difficulty
    ): MutableList<QuestionModel> {
        val list = mutableListOf<QuestionModel>()
        val diffName = difficulty.name.lowercase().replaceFirstChar { it.uppercase() }

        list.add(
            QuestionModel(
                id = 1,
                question = "In RBSE Class 10 $subjectName, what is the central foundational concept of '$chapterTitle'?",
                options = listOf(
                    "Core theoretical principles and definitions",
                    "Random unverified assertions",
                    "Outdated historical opinions",
                    "Non-syllabus extraneous topics"
                ),
                correctIndex = 0,
                explanation = "The chapter focuses on core definitions, theorems, and practical applications outlined in the RBSE Board syllabus."
            )
        )
        list.add(
            QuestionModel(
                id = 2,
                question = "Which of the following best represents an important textbook formula or rule studied in '$chapterTitle'?",
                options = listOf(
                    "Standard RBSE textbook standard method",
                    "Speculative intuition without proof",
                    "Non-standard informal shorthand",
                    "Disproven historical conjecture"
                ),
                correctIndex = 0,
                explanation = "Standard methods and theorems prescribed in RBSE NCERT textbooks form the basis of board exam marking."
            )
        )
        list.add(
            QuestionModel(
                id = 3,
                question = "When solving Board examination questions from '$chapterTitle', what step is essential for scoring full marks?",
                options = listOf(
                    "Writing formal formula, steps, and correct final units",
                    "Writing only direct answers without steps",
                    "Skipping definitions and reasons",
                    "Writing in colloquial language without terms"
                ),
                correctIndex = 0,
                explanation = "RBSE Class 10 evaluation rubrics award step-wise marks for formula, correct substitution, and units."
            )
        )
        list.add(
            QuestionModel(
                id = 4,
                question = "How is '$chapterTitle' categorized under the Class 10 blueprint?",
                options = listOf(
                    "Directly linked to prescribed syllabus unit objectives",
                    "Optional reading with zero weightage",
                    "Out of board exam scope",
                    "Non-assessable activity only"
                ),
                correctIndex = 0,
                explanation = "All prescribed chapters have allocated blueprint weightage in the annual secondary examination."
            )
        )
        list.add(
            QuestionModel(
                id = 5,
                question = "A key application of concepts learned in '$chapterTitle' helps students:",
                options = listOf(
                    "Solve analytical problems and build foundation for Class 11",
                    "Memorize without understanding",
                    "Avoid practical problems",
                    "Ignore step-wise logic"
                ),
                correctIndex = 0,
                explanation = "Conceptual clarity prepares students for senior secondary classes and competitive board standards."
            )
        )
        return list
    }
}
