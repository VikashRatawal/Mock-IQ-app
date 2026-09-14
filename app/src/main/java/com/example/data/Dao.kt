package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MockTrackerDao {

    // --- Mock Tests ---
    @Query("SELECT * FROM mock_tests ORDER BY date DESC, id DESC")
    fun getAllMockTestsFlow(): Flow<List<MockTestEntity>>

    @Query("SELECT * FROM mock_tests WHERE id = :id")
    suspend fun getMockTestById(id: Long): MockTestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockTest(mock: MockTestEntity): Long

    @Delete
    suspend fun deleteMockTest(mock: MockTestEntity)

    @Query("DELETE FROM mock_tests")
    suspend fun clearAllMockTests()

    // --- Question Records ---
    @Query("SELECT * FROM question_records ORDER BY qNo ASC")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM question_records WHERE mockId = :mockId ORDER BY qNo ASC")
    fun getQuestionsForMockFlow(mockId: Long): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM question_records WHERE mockId = :mockId ORDER BY qNo ASC")
    suspend fun getQuestionsForMockDirect(mockId: Long): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Delete
    suspend fun deleteQuestion(question: QuestionEntity)

    @Query("DELETE FROM question_records WHERE mockId = :mockId")
    suspend fun deleteQuestionsForMock(mockId: Long)

    @Query("DELETE FROM question_records")
    suspend fun clearAllQuestions()

    // --- Custom Reasons ---
    @Query("SELECT * FROM custom_reasons ORDER BY reason ASC")
    fun getAllCustomReasonsFlow(): Flow<List<CustomReasonEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomReason(reason: CustomReasonEntity)

    @Delete
    suspend fun deleteCustomReason(reason: CustomReasonEntity)

    // --- Custom Tags ---
    @Query("SELECT * FROM custom_tags ORDER BY tag ASC")
    fun getAllCustomTagsFlow(): Flow<List<CustomTagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomTag(tag: CustomTagEntity)

    @Delete
    suspend fun deleteCustomTag(tag: CustomTagEntity)
}
