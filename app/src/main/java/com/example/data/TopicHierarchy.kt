package com.example.data

object TopicHierarchy {
    // Subject -> Chapter -> List of Topics/Subtopics
    val hierarchy: Map<String, Map<String, List<String>>> = mapOf(
        "Quantitative Aptitude" to mapOf(
            "Arithmetic" to listOf(
                "Percentage (Successive %, Population, CP-SP %)",
                "Ratio & Proportion (Partnership, Variation)",
                "Mixture & Alligation (Replacement, Average)",
                "Profit & Loss (Discount, False Weight, Markup)",
                "Simple & Compound Interest (Installments, Difference)",
                "Time & Work (Efficiency,LCM, Pipes & Cisterns)",
                "Time Speed Distance (Relative, Trains, Boats & Streams)",
                "Average & Ages (Weighted, Group Average)"
            ),
            "Algebra" to listOf(
                "Linear & Quadratic Equations (Roots, Discriminant)",
                "Polynomials & Remainder Theorem",
                "Algebraic Identities (Identities & AM-GM)"
            ),
            "Geometry" to listOf(
                "Lines & Angles (Parallel, Transversal)",
                "Triangles (Similarity, Pythagoras, Centroid, Incentre)",
                "Circles (Chords, Tangents, Cyclic Quadrilateral)",
                "Polygons & Quadrilaterals"
            ),
            "Mensuration" to listOf(
                "2D Area & Perimeter (Triangle, Circles, Trapezius)",
                "3D Volume & Surface (Cone, Cylinder, Sphere, Frustum)"
            ),
            "Trigonometry" to listOf(
                "Trigonometric Ratios & Specific Angle Values",
                "Pythagorean Identities & Maximum-Minimum Values",
                "Height & Distance (Elevation & Depression)"
            ),
            "Number System" to listOf(
                "Divisibility Rules & LCM-HCF",
                "Factors & Remainders (Fermat, Wilson, Cyclicity)",
                "Surds, Indices & Rationalization"
            ),
            "Advanced Maths" to listOf(
                "Permutations & Combinations (Selection, Arrangement)",
                "Classical & Conditional Probability",
                "Sequence & Series (AP, GP, HP Sums)",
                "Venn Diagrams & Set Theory"
            ),
            "Data Interpretation" to listOf(
                "Tables & Pie Charts",
                "Bar & Line Graphs",
                "Mixed DI & Caselets"
            )
        ),
        "Reasoning" to mapOf(
            "Verbal Reasoning" to listOf(
                "Analogy (Number, Alphabet, Word)",
                "Classification (Odd One Out)",
                "Blood Relations (Family Tree, Coded)",
                "Coding-Decoding (Letter Shift, Symbol)"
            ),
            "Logical Reasoning" to listOf(
                "Syllogisms (All/Some/No)",
                "Direction & Distance & displacement",
                "Arrangements & Seating Layouts (Linear, Circular)",
                "Mathematical Operations & Matrix puzzles"
            ),
            "Non-Verbal Reasoning" to listOf(
                "Paper Folding & Cutting",
                "Embedded Figures & Mirror Images",
                "Series & Pattern Completion"
            )
        ),
        "English" to mapOf(
            "Grammar" to listOf(
                "Error Spotting & Sentence Improvement",
                "Active & Passive Voice Converter",
                "Direct & Indirect Speech Narrations"
            ),
            "Vocabulary" to listOf(
                "Synonyms & Antonyms (Tier 1/2/3)",
                "Idioms & Phrases",
                "One-Word Substitutions & Spelling Corrections"
            ),
            "Comprehension" to listOf(
                "Reading Comprehension (Passages, Inference)",
                "Cloze Tests (Contextual/Grammar blanks)",
                "Para Jumbles (Sentence Rearranging)"
            )
        ),
        "General Awareness" to mapOf(
            "General Science" to listOf(
                "Physics, Chemistry, Biology and Astronomy",
                "Computer Awareness & IT term"
            ),
            "Social Sciences" to listOf(
                "Ancient, Medieval & Indian Modern History",
                "Indian Polity, Constitution & Judiciary",
                "Physical, Indian & World Geography",
                "Indian Economy, Budgeting & Banking terms"
            ),
            "Static GK & Culture" to listOf(
                "Classical Dances, Music & Instruments",
                "Art, Festivals, UNESCO Sites",
                "Books & Authors, Capitals & Currencies",
                "National & International Parks, Rivers, Dams"
            ),
            "Current Affairs" to listOf(
                "Sports Achievements & Awards",
                "Government Schemes & Summits",
                "Appointments, Defence Exercises & MoUs"
            )
        )
    )

    val defaultReasons = listOf(
        "Perfect Solution" to "✨",
        "Calculation Error" to "🔢",
        "Silly Mistake" to "🤦",
        "Wrong Concept Applied" to "❓",
        "Formula Forgotten" to "📐",
        "Question Misread" to "👁️",
        "Time Pressure / Overtime" to "⚡",
        "Guess Work" to "🎲",
        "Revision Needed" to "📖",
        "Known but took too long" to "⏳"
    )

    val defaultTags = listOf(
        "Must Revise",
        "Trap Question",
        "Calculation Heavy",
        "Shortcut Available",
        "Concept Mastery Required",
        "Exam Favourite Pattern",
        "Silly Mistake Prone",
        "Good Level Question"
    )
}
