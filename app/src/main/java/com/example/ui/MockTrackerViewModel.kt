package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MockTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MockTrackerDatabase.getDatabase(application)
    private val repository = MockTrackerRepository(db.dao())

    // --- Database Source flows ---
    val mockTests: StateFlow<List<MockTestEntity>> = repository.allMockTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dbCustomReasons: StateFlow<List<CustomReasonEntity>> = repository.allCustomReasons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dbCustomTags: StateFlow<List<CustomTagEntity>> = repository.allCustomTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active/Current Session State ---
    private val _activeMockId = MutableStateFlow<Long?>(null)
    val activeMockId = _activeMockId.asStateFlow()

    // --- Active Mock Questions flow ---
    val activeMockQuestions: StateFlow<List<QuestionEntity>> = _activeMockId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getQuestionsForMock(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search / Filters UI state ---
    val questionSearchQuery = MutableStateFlow("")
    val questionStatusFilter = MutableStateFlow("All") // "All", "Correct", "Incorrect", "Unattempted"
    val questionDifficultyFilter = MutableStateFlow("All") // "All", "Very Easy", "Easy", "Medium", "Hard", "Very Hard"
    val questionChapterFilter = MutableStateFlow("All")

    val analyticsSubjectFilter = MutableStateFlow("All") // "All", "Quantitative Aptitude", "Reasoning", "English", "General Awareness"
    val analyticsTypeFilter = MutableStateFlow("All") // "All", "Sectional", "Full Mock"

    // --- Active Mock creation form state ---
    val setupDate = MutableStateFlow("")
    val setupName = MutableStateFlow("")
    val setupSubject = MutableStateFlow("Quantitative Aptitude")
    val setupType = MutableStateFlow("Sectional")
    val setupTotalQ = MutableStateFlow(25)
    val setupTotalTime = MutableStateFlow(30)

    // --- Active Question input form state ---
    val inputQNo = MutableStateFlow(1)
    val inputStatus = MutableStateFlow("Correct") // "Correct", "Incorrect", "Unattempted"
    val inputConfidence = MutableStateFlow("High") // "High", "Medium", "Low"
    val inputTimeTakenSeconds = MutableStateFlow(45)
    val inputTimeDeltaSeconds = MutableStateFlow(0)
    val inputDifficultyLevel = MutableStateFlow("Medium") // "Very Easy", "Easy", "Medium", "Hard", "Very Hard"
    val inputChapter = MutableStateFlow("")
    val inputTopic = MutableStateFlow("")
    val inputSubtopic = MutableStateFlow("")
    val inputReason = MutableStateFlow("")
    val inputSelectedTags = MutableStateFlow<Set<String>>(emptySet())
    val inputNotes = MutableStateFlow("")
    val inputFormula = MutableStateFlow("")

    val keepTopicOnNext = MutableStateFlow(false)
    val editingQuestionEntityId = MutableStateFlow<Long?>(null) // null means new question, not null means we're editing

    init {
        // Pre-set today's date in format
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        setupDate.value = sdf.format(java.util.Date())
    }

    // --- Mock Actions ---
    fun selectActiveMock(mockId: Long?) {
        _activeMockId.value = mockId
        if (mockId != null) {
            viewModelScope.launch {
                repository.getMockTestById(mockId)?.let { mock ->
                    setupDate.value = mock.date
                    setupName.value = mock.name
                    setupSubject.value = mock.subject
                    setupType.value = mock.type
                    setupTotalQ.value = mock.totalQuestions
                    setupTotalTime.value = mock.totalTimeMinutes
                }
                // Pre-populate next question number
                val currentQs = repository.getQuestionsForMockDirect(mockId)
                val nextQNo = if (currentQs.isEmpty()) 1 else (currentQs.maxOf { q -> q.qNo } + 1)
                inputQNo.value = nextQNo
            }
        }
    }

    fun saveMockSetup(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val score = 0.0 // computed based on question entities triggers later dynamically
            val percentile = 0.0 // computed dynamically as well

            val mock = MockTestEntity(
                id = _activeMockId.value ?: 0L,
                date = setupDate.value,
                name = setupName.value.ifBlank { "Mock-${System.currentTimeMillis() % 1000}" },
                subject = setupSubject.value,
                type = setupType.value,
                totalQuestions = setupTotalQ.value,
                totalTimeMinutes = setupTotalTime.value,
                score = score,
                percentile = percentile
            )
            val savedId = repository.insertMockTest(mock)
            _activeMockId.value = savedId
            inputQNo.value = 1
            onSuccess(savedId)
        }
    }

    fun createNewMock() {
        _activeMockId.value = null
        setupName.value = ""
        setupTotalQ.value = 25
        setupTotalTime.value = 30
        inputQNo.value = 1
        clearQuestionForm()
    }

    fun deleteMock(mock: MockTestEntity) {
        viewModelScope.launch {
            repository.deleteMockTest(mock)
            if (_activeMockId.value == mock.id) {
                _activeMockId.value = null
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllMockTests()
            _activeMockId.value = null
        }
    }

    // --- Question Actions ---
    fun saveQuestion(onComplete: () -> Unit = {}) {
        val mockId = _activeMockId.value ?: return
        viewModelScope.launch {
            val isEditing = editingQuestionEntityId.value != null
            val q = QuestionEntity(
                id = editingQuestionEntityId.value ?: 0L,
                mockId = mockId,
                qNo = inputQNo.value,
                status = inputStatus.value,
                chapter = inputChapter.value.trim(),
                topic = inputTopic.value.trim(),
                subtopic = inputSubtopic.value.trim(),
                difficultyLevel = inputDifficultyLevel.value,
                timeTakenSeconds = inputTimeTakenSeconds.value,
                timeDeltaSeconds = inputTimeDeltaSeconds.value,
                reason = inputReason.value,
                tags = inputSelectedTags.value.joinToString(","),
                notes = inputNotes.value.trim(),
                formula = inputFormula.value.trim(),
                confidence = inputConfidence.value
            )
            repository.insertQuestion(q)

            // Auto-update tags and reasons in database if custom
            if (q.tags.isNotEmpty()) {
                q.tags.split(",").forEach { tag ->
                    if (tag.isNotBlank() && tag !in TopicHierarchy.defaultTags) {
                        repository.insertCustomTag(tag.trim())
                    }
                }
            }
            if (q.reason.isNotBlank() && q.reason !in TopicHierarchy.defaultReasons.map { x -> x.first }) {
                repository.insertCustomReason(q.reason.trim())
            }

            // Dynamically recalculate mock total score and update MockTestEntity
            updateMockAggregates(mockId)

            // Advance form
            if (isEditing) {
                clearQuestionForm()
            } else {
                val nextQNo = inputQNo.value + 1
                if (!keepTopicOnNext.value) {
                    inputChapter.value = ""
                    inputTopic.value = ""
                    inputSubtopic.value = ""
                }
                editingQuestionEntityId.value = null
                inputQNo.value = nextQNo
                inputNotes.value = ""
                inputFormula.value = ""
                inputTimeTakenSeconds.value = 45
                inputTimeDeltaSeconds.value = 0
            }
            onComplete()
        }
    }

    suspend fun updateMockAggregates(mockId: Long) {
        val qs = repository.getQuestionsForMockDirect(mockId)
        val correctCount = qs.count { it.status == "Correct" }
        val incorrectCount = qs.count { it.status == "Incorrect" }
        val rawScore = (correctCount * 2.0) - (incorrectCount * 0.5)

        // Estimated percentile/accuracy base
        val totalEntered = qs.size
        val accuracy = if (totalEntered > 0) (correctCount.toDouble() / totalEntered.toDouble()) * 100.0 else 0.0

        repository.getMockTestById(mockId)?.let { mock ->
            val updatedMock = mock.copy(
                score = rawScore,
                percentile = accuracy // Storing accuracy % directly in percentile placeholder if percentile not provided manually
            )
            repository.insertMockTest(updatedMock)
        }
    }

    fun deleteQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
            updateMockAggregates(question.mockId)
        }
    }

    fun startEditingQuestion(question: QuestionEntity) {
        editingQuestionEntityId.value = question.id
        inputQNo.value = question.qNo
        inputStatus.value = question.status
        inputConfidence.value = question.confidence
        inputTimeTakenSeconds.value = question.timeTakenSeconds
        inputTimeDeltaSeconds.value = question.timeDeltaSeconds
        inputDifficultyLevel.value = question.difficultyLevel
        inputChapter.value = question.chapter
        inputTopic.value = question.topic
        inputSubtopic.value = question.subtopic
        inputReason.value = question.reason
        inputSelectedTags.value = question.tags.split(",").filter { it.isNotBlank() }.toSet()
        inputNotes.value = question.notes
        inputFormula.value = question.formula
    }

    fun clearQuestionForm() {
        editingQuestionEntityId.value = null
        inputQNo.value = 1
        inputStatus.value = "Correct"
        inputConfidence.value = "High"
        inputTimeTakenSeconds.value = 45
        inputTimeDeltaSeconds.value = 0
        inputDifficultyLevel.value = "Medium"
        inputChapter.value = ""
        inputTopic.value = ""
        inputSubtopic.value = ""
        inputReason.value = ""
        inputSelectedTags.value = emptySet()
        inputNotes.value = ""
        inputFormula.value = ""
    }

    // --- Custom Reason & Custom Tag Addition ---
    fun addCustomReason(reason: String) {
        viewModelScope.launch {
            repository.insertCustomReason(reason.trim())
        }
    }

    fun deleteCustomReason(reason: String) {
        viewModelScope.launch {
            repository.deleteCustomReason(reason.trim())
        }
    }

    fun addCustomTag(tag: String) {
        viewModelScope.launch {
            repository.insertCustomTag(tag.trim())
        }
    }

    fun deleteCustomTag(tag: String) {
        viewModelScope.launch {
            repository.deleteCustomTag(tag.trim())
        }
    }

    fun toggleTagSelection(tag: String) {
        val current = inputSelectedTags.value.toMutableSet()
        if (tag in current) {
            current.remove(tag)
        } else {
            current.add(tag)
        }
        inputSelectedTags.value = current
    }

    // --- Fast/Bulk Entry Parsing ---
    fun saveFastBulk(bulkText: String, onSuccess: (Int, Int) -> Unit, onError: (String) -> Unit) {
        val mockId = _activeMockId.value
        if (mockId == null) {
            onError("No active Mock Test session. Please save Mock Setup first!")
            return
        }

        viewModelScope.launch {
            val lines = bulkText.trim().split("\n").filter { it.isNotBlank() }
            if (lines.isEmpty()) {
                onError("Paste text is empty.")
                return@launch
            }

            var saved = 0
            var overwritten = 0
            val existingQs = repository.getQuestionsForMockDirect(mockId).associateBy { it.qNo }
            var nextQNo = if (existingQs.isEmpty()) 1 else (existingQs.keys.maxOrNull() ?: 0) + 1

            for (line in lines) {
                try {
                    val parsed = parseFastLine(line, nextQNo)
                    val qNo = parsed.qNo

                    val existing = existingQs[qNo]
                    val qToInsert = QuestionEntity(
                        id = existing?.id ?: 0L,
                        mockId = mockId,
                        qNo = qNo,
                        status = parsed.status,
                        chapter = parsed.chapter,
                        topic = parsed.topic,
                        subtopic = parsed.subtopic,
                        difficultyLevel = parsed.difficultyLevel,
                        timeTakenSeconds = parsed.timeTakenSeconds,
                        timeDeltaSeconds = parsed.timeDeltaSeconds,
                        reason = parsed.reason,
                        tags = parsed.tags,
                        notes = parsed.notes,
                        formula = parsed.formula,
                        confidence = parsed.confidence
                    )
                    repository.insertQuestion(qToInsert)

                    if (existing != null) overwritten++ else saved++
                    nextQNo = maxOf(nextQNo, qNo + 1)
                } catch (e: Exception) {
                    // skip malformed line, or could log
                }
            }

            updateMockAggregates(mockId)
            inputQNo.value = nextQNo
            onSuccess(saved, overwritten)
        }
    }

    private fun parseFastLine(line: String, defaultQNo: Int): ParsedQuestion {
        // Formats supported:
        // 1. Key Value pairs: "Q=1 | S=C | Ch=Arithmetic | Topic=Percentage | ..."
        // 2. Short positional: "1 | C | Arithmetic > Percentage > Successive % | Easy | 45s | Silly Mistake | High | Must Revise | Note"
        val parts = line.split("|").map { it.trim() }.filter { it.isNotBlank() }
        val hasKV = parts.any { it.contains("=") || it.contains(":") }

        if (hasKV) {
            var qNo = defaultQNo
            var status = "Correct"
            var chapter = ""
            var topic = ""
            var subtopic = ""
            var level = "Medium"
            var timeTaken = 45
            var timeDelta = 0
            var reason = ""
            var confidence = "High"
            var tags = ""
            var notes = ""
            var formula = ""

            parts.forEach { part ->
                val separator = if (part.contains("=")) "=" else ":"
                val subParts = part.split(separator, limit = 2).map { it.trim() }
                if (subParts.size == 2) {
                    val key = subParts[0].lowercase().replace(" ", "").replace("_", "")
                    val valStr = subParts[1]
                    when (key) {
                        "q", "qno", "number", "question" -> qNo = valStr.toIntOrNull() ?: qNo
                        "s", "status", "result" -> status = normStatus(valStr)
                        "ch", "chapter" -> chapter = valStr
                        "topic", "tp" -> topic = valStr
                        "sub", "subtopic" -> subtopic = valStr
                        "level", "diff", "difficulty" -> level = normDifficulty(valStr)
                        "time", "t", "timetaken" -> timeTaken = parseTimeSeconds(valStr)
                        "dt", "delta", "timedelta", "overtime" -> timeDelta = valStr.toIntOrNull() ?: 0
                        "reason", "r", "mistake" -> reason = valStr
                        "conf", "confidence" -> confidence = normConfidence(valStr)
                        "tag", "tags" -> tags = valStr.split(",").map { it.trim() }.filter { it.isNotBlank() }.joinToString(",")
                        "note", "notes" -> notes = valStr
                        "formula", "f" -> formula = valStr
                    }
                }
            }
            return ParsedQuestion(qNo, status, chapter, topic, subtopic, level, timeTaken, timeDelta, reason, tags, notes, formula, confidence)
        } else {
            // Positional format
            val qNo = parts.getOrNull(0)?.toIntOrNull() ?: defaultQNo
            val status = normStatus(parts.getOrNull(1) ?: "Correct")
            val classPath = parts.getOrNull(2) ?: ""
            val classParts = classPath.split(">").map { it.trim() }
            val chapter = classParts.getOrNull(0) ?: ""
            val topic = classParts.getOrNull(1) ?: ""
            val subtopic = classParts.getOrNull(2) ?: ""

            val level = normDifficulty(parts.getOrNull(3) ?: "Medium")
            val timeTaken = parseTimeSeconds(parts.getOrNull(4) ?: "45s")
            val reason = parts.getOrNull(5) ?: ""
            val confidence = normConfidence(parts.getOrNull(6) ?: "High")
            val tags = (parts.getOrNull(7) ?: "").split(",").map { it.trim() }.filter { it.isNotBlank() }.joinToString(",")
            val notes = parts.getOrNull(8) ?: ""

            return ParsedQuestion(qNo, status, chapter, topic, subtopic, level, timeTaken, 0, reason, tags, notes, "", confidence)
        }
    }

    private fun normStatus(input: String): String {
        return when (input.lowercase().trim()) {
            "c", "correct", "right", "yes", "1" -> "Correct"
            "i", "w", "wrong", "incorrect", "no", "0" -> "Incorrect"
            "u", "skip", "skipped", "unattempted", "blank" -> "Unattempted"
            else -> "Correct"
        }
    }

    private fun normDifficulty(input: String): String {
        return when (input.lowercase().trim().replace(" ", "")) {
            "veryeasy", "ve", "v.easy" -> "Very Easy"
            "easy", "e" -> "Easy"
            "medium", "m", "med" -> "Medium"
            "hard", "h" -> "Hard"
            "veryhard", "vh", "v.hard" -> "Very Hard"
            else -> "Medium"
        }
    }

    private fun normConfidence(input: String): String {
        return when (input.lowercase().trim()) {
            "high", "h", "good" -> "High"
            "medium", "med", "m" -> "Medium"
            "low", "l", "poor" -> "Low"
            else -> "High"
        }
    }

    private fun parseTimeSeconds(input: String): Int {
        val str = input.lowercase().trim()
        if (str.isEmpty()) return 45
        if (str.contains(":")) {
            val parts = str.split(":")
            if (parts.size == 2) {
                val m = parts[0].toIntOrNull() ?: 0
                val s = parts[1].toIntOrNull() ?: 0
                return m * 60 + s
            }
        }
        val num = str.replace("s", "").replace("sec", "").replace("m", "").toIntOrNull() ?: 45
        if (str.contains("m")) return num * 60
        return num
    }

    data class ParsedQuestion(
        val qNo: Int,
        val status: String,
        val chapter: String,
        val topic: String,
        val subtopic: String,
        val difficultyLevel: String,
        val timeTakenSeconds: Int,
        val timeDeltaSeconds: Int,
        val reason: String,
        val tags: String,
        val notes: String,
        val formula: String,
        val confidence: String
    )
}
