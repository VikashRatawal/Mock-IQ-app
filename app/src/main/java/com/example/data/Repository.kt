package com.example.data

import kotlinx.coroutines.flow.Flow

class MockTrackerRepository(private val dao: MockTrackerDao) {

    // --- Mock Tests ---
    val allMockTests: Flow<List<MockTestEntity>> = dao.getAllMockTestsFlow()

    suspend fun getMockTestById(id: Long): MockTestEntity? = dao.getMockTestById(id)

    suspend fun insertMockTest(mock: MockTestEntity): Long = dao.insertMockTest(mock)

    suspend fun deleteMockTest(mock: MockTestEntity) {
        // Cascade manually for simplicity and speed
        dao.deleteQuestionsForMock(mock.id)
        dao.deleteMockTest(mock)
    }

    suspend fun clearAllMockTests() {
        dao.clearAllQuestions()
        dao.clearAllMockTests()
    }

    // --- Question Records ---
    val allQuestions: Flow<List<QuestionEntity>> = dao.getAllQuestionsFlow()

    fun getQuestionsForMock(mockId: Long): Flow<List<QuestionEntity>> = dao.getQuestionsForMockFlow(mockId)

    suspend fun getQuestionsForMockDirect(mockId: Long): List<QuestionEntity> = dao.getQuestionsForMockDirect(mockId)

    suspend fun insertQuestion(question: QuestionEntity): Long = dao.insertQuestion(question)

    suspend fun deleteQuestion(question: QuestionEntity) = dao.deleteQuestion(question)

    suspend fun deleteQuestionsForMock(mockId: Long) = dao.deleteQuestionsForMock(mockId)

    // --- Custom Reasons ---
    val allCustomReasons: Flow<List<CustomReasonEntity>> = dao.getAllCustomReasonsFlow()

    suspend fun insertCustomReason(reason: String) {
        dao.insertCustomReason(CustomReasonEntity(reason))
    }

    suspend fun deleteCustomReason(reason: String) {
        dao.deleteCustomReason(CustomReasonEntity(reason))
    }

    // --- Custom Tags ---
    val allCustomTags: Flow<List<CustomTagEntity>> = dao.getAllCustomTagsFlow()

    suspend fun insertCustomTag(tag: String) {
        dao.insertCustomTag(CustomTagEntity(tag))
    }

    suspend fun deleteCustomTag(tag: String) {
        dao.deleteCustomTag(CustomTagEntity(tag))
    }
}
