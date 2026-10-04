package com.example.data

import com.example.data.local.ExcuseDao
import com.example.data.local.ExcuseEntity
import com.example.data.local.UserPreferences
import com.example.data.model.ExcuseGenerationResult
import com.example.data.model.ExcuseOption
import com.example.data.model.ExcuseType
import com.example.network.GeminiService
import kotlinx.coroutines.flow.Flow

class ExcuseRepository(
    private val excuseDao: ExcuseDao,
    private val userPreferences: UserPreferences,
    private val geminiService: GeminiService = GeminiService()
) {
    val allHistory: Flow<List<ExcuseEntity>> = excuseDao.getAllExcuses()

    fun searchHistory(query: String): Flow<List<ExcuseEntity>> =
        if (query.isBlank()) excuseDao.getAllExcuses() else excuseDao.searchExcuses(query)

    suspend fun generateExcuses(
        situation: String,
        category: String,
        recipient: String,
        severityLevel: Int,
        vibe: String
    ): ExcuseGenerationResult {
        val aiResponse = geminiService.generateExcuses(
            situation = situation,
            category = category,
            recipient = recipient,
            severityLevel = severityLevel,
            vibe = vibe,
            apiKeyOverride = userPreferences.customApiKey.value,
            modelOverride = userPreferences.selectedModel.value
        )

        userPreferences.recordGeneration(category, vibe)

        // Save to Room database
        val safeOption = aiResponse.excuses.firstOrNull { it.type == ExcuseType.SAFE }
            ?: aiResponse.excuses.firstOrNull()
        val funnyOption = aiResponse.excuses.firstOrNull { it.type == ExcuseType.FUNNY }
            ?: aiResponse.excuses.getOrNull(1) ?: safeOption
        val unhingedOption = aiResponse.excuses.firstOrNull { it.type == ExcuseType.UNHINGED }
            ?: aiResponse.excuses.lastOrNull() ?: safeOption

        val entity = ExcuseEntity(
            situation = situation,
            category = category,
            recipient = recipient,
            severityLevel = severityLevel,
            vibe = vibe,
            safeText = safeOption?.text.orEmpty(),
            safeBelievability = safeOption?.believability ?: 85,
            safeRisk = safeOption?.risk ?: 15,
            funnyText = funnyOption?.text.orEmpty(),
            funnyBelievability = funnyOption?.believability ?: 55,
            funnyRisk = funnyOption?.risk ?: 35,
            unhingedText = unhingedOption?.text.orEmpty(),
            unhingedBelievability = unhingedOption?.believability ?: 15,
            unhingedRisk = unhingedOption?.risk ?: 85,
            overallBelievability = aiResponse.overallBelievability,
            overallRisk = aiResponse.overallRisk,
            overallDrama = aiResponse.overallDrama,
            overallEffort = aiResponse.overallEffort
        )

        val insertedId = excuseDao.insertExcuse(entity)

        return ExcuseGenerationResult(
            id = insertedId,
            situation = situation,
            category = category,
            recipient = recipient,
            severityLevel = severityLevel,
            vibe = vibe,
            excuses = aiResponse.excuses,
            believabilityScore = aiResponse.overallBelievability,
            riskScore = aiResponse.overallRisk,
            dramaScore = aiResponse.overallDrama,
            effortScore = aiResponse.overallEffort
        )
    }

    suspend fun deleteHistoryItem(id: Long) {
        excuseDao.deleteExcuseById(id)
    }

    suspend fun clearAllHistory() {
        excuseDao.clearAll()
    }

    fun recordCopy() {
        userPreferences.recordCopy()
    }
}
