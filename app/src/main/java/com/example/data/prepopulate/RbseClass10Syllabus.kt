package com.example.data.prepopulate

import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.SubjectEntity
import com.example.model.ChapterStatus
import com.example.model.QuestionModel

object RbseClass10Syllabus {

    data class PrepopulatedSubject(
        val subject: SubjectEntity,
        val chapters: List<PrepopulatedChapter>
    )

    data class PrepopulatedChapter(
        val chapterNumber: Int,
        val titleEn: String,
        val titleHi: String,
        val defaultPassingPercent: Int = 75
    )

    val INITIAL_DATA: List<PrepopulatedSubject> = listOf(
        PrepopulatedSubject(
            subject = SubjectEntity(
                id = 1,
                nameEn = "Mathematics",
                nameHi = "गणित",
                code = "MATH_10",
                iconName = "calculate",
                displayOrder = 1
            ),
            chapters = listOf(
                PrepopulatedChapter(1, "Real Numbers", "वास्तविक संख्याएँ"),
                PrepopulatedChapter(2, "Polynomials", "बहुपद"),
                PrepopulatedChapter(3, "Pair of Linear Equations in Two Variables", "दो चर वाले रैखिक समीकरण युग्म"),
                PrepopulatedChapter(4, "Quadratic Equations", "द्विघात समीकरण"),
                PrepopulatedChapter(5, "Arithmetic Progressions", "समांतर श्रेणियाँ"),
                PrepopulatedChapter(6, "Triangles", "त्रिभुज"),
                PrepopulatedChapter(7, "Coordinate Geometry", "निर्देशांक ज्यामिति"),
                PrepopulatedChapter(8, "Introduction to Trigonometry", "त्रिकोणमिति का परिचय"),
                PrepopulatedChapter(9, "Some Applications of Trigonometry", "त्रिकोणमिति के कुछ अनुप्रयोग"),
                PrepopulatedChapter(10, "Circles", "वृत्त"),
                PrepopulatedChapter(11, "Areas Related to Circles", "वृत्तों से संबंधित क्षेत्रफल"),
                PrepopulatedChapter(12, "Surface Areas and Volumes", "पृष्ठीय क्षेत्रफल और आयतन"),
                PrepopulatedChapter(13, "Statistics", "सांख्यिकी"),
                PrepopulatedChapter(14, "Probability", "प्रायिकता")
            )
        ),
        PrepopulatedSubject(
            subject = SubjectEntity(
                id = 2,
                nameEn = "Science",
                nameHi = "विज्ञान",
                code = "SCI_10",
                iconName = "science",
                displayOrder = 2
            ),
            chapters = listOf(
                PrepopulatedChapter(1, "Chemical Reactions and Equations", "रासायनिक अभिक्रियाएँ एवं समीकरण"),
                PrepopulatedChapter(2, "Acids, Bases and Salts", "अम्ल, क्षारक एवं लवण"),
                PrepopulatedChapter(3, "Metals and Non-metals", "धातु एवं अधातु"),
                PrepopulatedChapter(4, "Carbon and its Compounds", "कार्बन एवं उसके यौगिक"),
                PrepopulatedChapter(5, "Life Processes", "जैव प्रक्रम"),
                PrepopulatedChapter(6, "Control and Coordination", "नियंत्रण एवं समन्वय"),
                PrepopulatedChapter(7, "How do Organisms Reproduce?", "जीव जनन कैसे करते हैं?"),
                PrepopulatedChapter(8, "Heredity", "आनुवंशिकता"),
                PrepopulatedChapter(9, "Light - Reflection and Refraction", "प्रकाश - परावर्तन तथा अपवर्तन"),
                PrepopulatedChapter(10, "The Human Eye and Colourful World", "मानव नेत्र तथा रंगबिरंगा संसार"),
                PrepopulatedChapter(11, "Electricity", "विद्युत"),
                PrepopulatedChapter(12, "Magnetic Effects of Electric Current", "विद्युत धारा के चुंबकीय प्रभाव"),
                PrepopulatedChapter(13, "Our Environment", "हमारा पर्यावरण")
            )
        ),
        PrepopulatedSubject(
            subject = SubjectEntity(
                id = 3,
                nameEn = "Social Science (SST)",
                nameHi = "सामाजिक विज्ञान",
                code = "SST_10",
                iconName = "public",
                displayOrder = 3
            ),
            chapters = listOf(
                PrepopulatedChapter(1, "Rise of Nationalism in Europe", "यूरोप में राष्ट्रवाद का उदय"),
                PrepopulatedChapter(2, "Nationalism in India", "भारत में राष्ट्रवाद"),
                PrepopulatedChapter(3, "The Making of a Global World", "भूमंडलीकृत विश्व का बनना"),
                PrepopulatedChapter(4, "The Age of Industrialisation", "औद्योगिकीकरण का युग"),
                PrepopulatedChapter(5, "Resources and Development", "संसाधन एवं विकास"),
                PrepopulatedChapter(6, "Forest and Wildlife Resources", "वन एवं वन्य जीव संसाधन"),
                PrepopulatedChapter(7, "Water Resources", "जल संसाधन"),
                PrepopulatedChapter(8, "Agriculture", "कृषि"),
                PrepopulatedChapter(9, "Minerals and Energy Resources", "खनिज तथा ऊर्जा संसाधन"),
                PrepopulatedChapter(10, "Power Sharing", "सत्ता की साझेदारी"),
                PrepopulatedChapter(11, "Federalism", "संघवाद"),
                PrepopulatedChapter(12, "Gender, Religion and Caste", "जाति, धर्म और लैंगिक मसले"),
                PrepopulatedChapter(13, "Political Parties", "राजनीतिक दल"),
                PrepopulatedChapter(14, "Development", "विकास"),
                PrepopulatedChapter(15, "Sectors of the Indian Economy", "भारतीय अर्थव्यवस्था के क्षेत्रक"),
                PrepopulatedChapter(16, "Money and Credit", "मुद्रा और साख")
            )
        ),
        PrepopulatedSubject(
            subject = SubjectEntity(
                id = 4,
                nameEn = "Hindi",
                nameHi = "हिंदी",
                code = "HIN_10",
                iconName = "menu_book",
                displayOrder = 4
            ),
            chapters = listOf(
                PrepopulatedChapter(1, "Pad (Surdas)", "पद (सूरदास)"),
                PrepopulatedChapter(2, "Ram-Lakshman-Parshuram Samvad (Tulsidas)", "राम-लक्ष्मण-परशुराम संवाद (तुलसीदास)"),
                PrepopulatedChapter(3, "Aatmakathya (Jaishankar Prasad)", "आत्मकथ्य (जयशंकर प्रसाद)"),
                PrepopulatedChapter(4, "Utsah and Att Nahi Rahi Hai (Nirala)", "उत्साह और अट नहीं रही है (निराला)"),
                PrepopulatedChapter(5, "Netaji Ka Chashma (Swayam Prakash)", "नेताजी का चश्मा (स्वयं प्रकाश)"),
                PrepopulatedChapter(6, "Balgobin Bhagat (Ramvriksh Benipuri)", "बालगोबिन भगत (रामवृक्ष बेनीपुरी)"),
                PrepopulatedChapter(7, "Lakhnavi Andaz (Yashpal)", "लखनवी अंदाज़ (यशपाल)"),
                PrepopulatedChapter(8, "Mata Ka Aanchal (Shivpujan Sahay)", "माता का आँचल (शिवपूजन सहाय)"),
                PrepopulatedChapter(9, "Sana Sana Hath Jodi (Madhu Kankariya)", "साना साना हाथ जोड़ि (मधु कांकरिया)"),
                PrepopulatedChapter(10, "Hindi Grammar (Sandhi, Samas, Vakya)", "व्यावहारिक व्याकरण (संधि, समास, वाक्य)")
            )
        ),
        PrepopulatedSubject(
            subject = SubjectEntity(
                id = 5,
                nameEn = "English",
                nameHi = "अंग्रेजी",
                code = "ENG_10",
                iconName = "language",
                displayOrder = 5
            ),
            chapters = listOf(
                PrepopulatedChapter(1, "A Letter to God", "A Letter to God"),
                PrepopulatedChapter(2, "Nelson Mandela: Long Walk to Freedom", "Nelson Mandela: Long Walk to Freedom"),
                PrepopulatedChapter(3, "Two Stories about Flying", "Two Stories about Flying"),
                PrepopulatedChapter(4, "From the Diary of Anne Frank", "From the Diary of Anne Frank"),
                PrepopulatedChapter(5, "Glimpses of India", "Glimpses of India"),
                PrepopulatedChapter(6, "Madam Rides the Bus", "Madam Rides the Bus"),
                PrepopulatedChapter(7, "The Sermon at Benares", "The Sermon at Benares"),
                PrepopulatedChapter(8, "A Triumph of Surgery", "A Triumph of Surgery"),
                PrepopulatedChapter(9, "The Thief's Story", "The Thief's Story"),
                PrepopulatedChapter(10, "Footprints without Feet", "Footprints without Feet"),
                PrepopulatedChapter(11, "The Necklace", "The Necklace"),
                PrepopulatedChapter(12, "Grammar (Tenses, Reported Speech, Modals)", "Grammar & Composition")
            )
        ),
        PrepopulatedSubject(
            subject = SubjectEntity(
                id = 6,
                nameEn = "Sanskrit",
                nameHi = "संस्कृत",
                code = "SAN_10",
                iconName = "auto_stories",
                displayOrder = 6
            ),
            chapters = listOf(
                PrepopulatedChapter(1, "Shuchi Paryavaranam", "शुचिपर्यावरणम्"),
                PrepopulatedChapter(2, "Buddhir Balawati Sada", "बुद्धिर्बलवती सदा"),
                PrepopulatedChapter(3, "Janani Tulyavatsala", "जननी तुल्यवत्सला"),
                PrepopulatedChapter(4, "Subhashitani", "सुभाषितानि"),
                PrepopulatedChapter(5, "Sauhardam Prakriteh Shobha", "सौहार्दं प्रकृतेः शोभा"),
                PrepopulatedChapter(6, "Vichitrah Sakshi", "विचित्रः साक्षी"),
                PrepopulatedChapter(7, "Sanskrit Grammar (Karak, Sandhi, Pratyay)", "संस्कृत व्याकरणम् (कारक, संधि, प्रत्यय)")
            )
        ),
        PrepopulatedSubject(
            subject = SubjectEntity(
                id = 7,
                nameEn = "Information Technology (IT)",
                nameHi = "सूचना प्रौद्योगिकी",
                code = "IT_10",
                iconName = "computer",
                displayOrder = 7,
                isOptional = true
            ),
            chapters = listOf(
                PrepopulatedChapter(1, "Digital Documentation (Advanced)", "डिजिटल दस्तावेज़ीकरण (उन्नत)"),
                PrepopulatedChapter(2, "Electronic Spreadsheet (Advanced)", "इलेक्ट्रॉनिक स्प्रेडशीट (उन्नत)"),
                PrepopulatedChapter(3, "Database Management System (DBMS)", "डेटाबेस प्रबंधन प्रणाली (DBMS)"),
                PrepopulatedChapter(4, "Web Applications and Security", "वेब अनुप्रयोग और सुरक्षा")
            )
        )
    )

    fun toEntities(): Pair<List<SubjectEntity>, List<ChapterEntity>> {
        val subjects = mutableListOf<SubjectEntity>()
        val chapters = mutableListOf<ChapterEntity>()

        INITIAL_DATA.forEach { item ->
            subjects.add(item.subject)
            item.chapters.forEachIndexed { index, ch ->
                chapters.add(
                    ChapterEntity(
                        id = (item.subject.id * 100) + ch.chapterNumber,
                        subjectId = item.subject.id,
                        chapterNumber = ch.chapterNumber,
                        titleEn = ch.titleEn,
                        titleHi = ch.titleHi,
                        status = ChapterStatus.NOT_STARTED.name,
                        configuredPassingPercent = ch.defaultPassingPercent,
                        isHidden = false,
                        displayOrder = index + 1
                    )
                )
            }
        }
        return Pair(subjects, chapters)
    }
}
