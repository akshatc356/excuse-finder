package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ExcuseRepository
import com.example.data.local.ExcuseDatabase
import com.example.data.local.ExcuseEntity
import com.example.data.local.UserPreferences
import com.example.data.model.Category
import com.example.data.model.ExcuseGenerationResult
import com.example.data.model.ExcuseOption
import com.example.data.model.ExcuseType
import com.example.data.model.Recipient
import com.example.data.model.Vibe
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    SCANNER,
    RESULTS,
    HISTORY,
    PROFILE
}

class ExcuseViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val database = ExcuseDatabase.getInstance(application)
    private val repository = ExcuseRepository(database.excuseDao(), userPreferences)

    // Navigation State
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val navigationBackStack = mutableListOf<Screen>()

    // Setup Questionnaire State
    val situationInput = MutableStateFlow("")
    val selectedCategory = MutableStateFlow(Category.LATE)
    val selectedRecipient = MutableStateFlow(Recipient.FRIEND)
    val severityLevel = MutableStateFlow(2) // 1: NOT BAD, 2: UH OH, 3: I'M COOKED
    val selectedVibe = MutableStateFlow(Vibe.BELIEVABLE)

    val isSetupSheetVisible = MutableStateFlow(false)

    // Scanner & Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _scannerStepText = MutableStateFlow("ANALYZING SITUATION...")
    val scannerStepText: StateFlow<String> = _scannerStepText.asStateFlow()

    private val _scannerKeyword = MutableStateFlow("CONTEXT")
    val scannerKeyword: StateFlow<String> = _scannerKeyword.asStateFlow()

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError.asStateFlow()

    // Results State
    private val _currentResult = MutableStateFlow<ExcuseGenerationResult?>(null)
    val currentResult: StateFlow<ExcuseGenerationResult?> = _currentResult.asStateFlow()

    // Active excuse selected for the primary copy button
    val selectedExcuseIndex = MutableStateFlow(0)

    // History State
    val historySearchQuery = MutableStateFlow("")
    private val _historyList = MutableStateFlow<List<ExcuseEntity>>(emptyList())
    val historyList: StateFlow<List<ExcuseEntity>> = _historyList.asStateFlow()

    // Operator Profile & Preferences
    val userName: StateFlow<String> = userPreferences.userName
    val userBio: StateFlow<String> = userPreferences.userBio
    val userAvatar: StateFlow<String> = userPreferences.userAvatar
    val defaultRecipient: StateFlow<String> = userPreferences.defaultRecipient
    val defaultVibe: StateFlow<String> = userPreferences.defaultVibe
    val totalGenerated: StateFlow<Int> = userPreferences.totalGenerated
    val totalCopied: StateFlow<Int> = userPreferences.totalCopied
    val mostUsedCategory: StateFlow<String> = userPreferences.mostUsedCategory
    val favoriteVibe: StateFlow<String> = userPreferences.favoriteVibe
    val animationIntensity: StateFlow<String> = userPreferences.animationIntensity
    val hapticsEnabled: StateFlow<Boolean> = userPreferences.hapticsEnabled
    val customApiKey: StateFlow<String> = userPreferences.customApiKey
    val selectedModel: StateFlow<String> = userPreferences.selectedModel
    val customModel: StateFlow<String> = userPreferences.selectedModel

    // Feedback
    private val _copyFeedback = MutableStateFlow<String?>(null)
    val copyFeedback: StateFlow<String?> = _copyFeedback.asStateFlow()

    private var scannerJob: Job? = null

    init {
        // Apply default preferences if set
        val savedRecipientTitle = userPreferences.defaultRecipient.value
        Recipient.values().firstOrNull { it.title.equals(savedRecipientTitle, ignoreCase = true) }?.let {
            selectedRecipient.value = it
        }

        val savedVibeTitle = userPreferences.defaultVibe.value
        Vibe.values().firstOrNull { it.title.equals(savedVibeTitle, ignoreCase = true) }?.let {
            selectedVibe.value = it
        }

        viewModelScope.launch {
            historySearchQuery.collectLatest { query ->
                repository.searchHistory(query).collect { list ->
                    _historyList.value = list
                }
            }
        }
    }

    fun setUserName(name: String) {
        userPreferences.setUserName(name)
    }

    fun setUserBio(bio: String) {
        userPreferences.setUserBio(bio)
    }

    fun setUserAvatar(avatar: String) {
        userPreferences.setUserAvatar(avatar)
    }

    fun setDefaultRecipient(recipient: String) {
        userPreferences.setDefaultRecipient(recipient)
        Recipient.values().firstOrNull { it.title.equals(recipient, ignoreCase = true) }?.let {
            selectedRecipient.value = it
        }
    }

    fun setDefaultVibe(vibe: String) {
        userPreferences.setDefaultVibe(vibe)
        Vibe.values().firstOrNull { it.title.equals(vibe, ignoreCase = true) }?.let {
            selectedVibe.value = it
        }
    }

    fun resetProfileStats() {
        userPreferences.clearStats()
    }

    fun setCustomApiKey(key: String) {
        userPreferences.setCustomApiKey(key)
    }

    fun setSelectedModel(model: String) {
        userPreferences.setSelectedModel(model)
    }

    fun setCustomModel(model: String) {
        userPreferences.setSelectedModel(model)
    }

    fun updateSituation(text: String) {
        situationInput.value = text
    }

    fun selectCategory(category: Category) {
        selectedCategory.value = category
    }

    fun selectRecipient(recipient: Recipient) {
        selectedRecipient.value = recipient
    }

    fun setSeverityLevel(level: Int) {
        severityLevel.value = level
    }

    fun setVibe(vibe: Vibe) {
        selectedVibe.value = vibe
    }

    fun startExcuseGeneration() {
        startGeneration()
    }

    fun selectExcuseIndex(index: Int) {
        selectedExcuseIndex.value = index
    }

    fun copyExcuse(text: String) {
        repository.recordCopy()
        _copyFeedback.value = "COPIED ✓"
    }

    fun searchHistory(query: String) {
        historySearchQuery.value = query
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun toggleHaptics(enabled: Boolean) {
        userPreferences.setHapticsEnabled(enabled)
    }

    fun isGeminiConfigured(): Boolean {
        val custom = userPreferences.customApiKey.value
        val injected = com.example.BuildConfig.GEMINI_API_KEY
        return custom.isNotBlank() || (injected.isNotBlank() && injected != "MY_GEMINI_API_KEY")
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            navigationBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (isSetupSheetVisible.value) {
            isSetupSheetVisible.value = false
            return true
        }
        if (navigationBackStack.isNotEmpty()) {
            _currentScreen.value = navigationBackStack.removeAt(navigationBackStack.size - 1)
            return true
        } else if (_currentScreen.value != Screen.HOME) {
            _currentScreen.value = Screen.HOME
            return true
        }
        return false
    }

    fun openSetupSheet() {
        val prompt = situationInput.value.trim()
        if (prompt.isEmpty()) {
            situationInput.value = "I'm 2 hours late and need damage control."
        }
        isSetupSheetVisible.value = true
    }

    fun closeSetupSheet() {
        isSetupSheetVisible.value = false
    }

    fun startGeneration() {
        isSetupSheetVisible.value = false
        _generationError.value = null
        navigateTo(Screen.SCANNER)
        _isGenerating.value = true

        val situation = situationInput.value.ifBlank { "I need an urgent excuse." }
        val category = selectedCategory.value.title
        val recipient = selectedRecipient.value.title
        val severity = severityLevel.value
        val vibe = selectedVibe.value.title

        scannerJob?.cancel()
        scannerJob = viewModelScope.launch {
            val keywords = listOf(
                "CONTEXT", "RISK", "DRAMA", "TIMELINE",
                "SOCIAL DAMAGE", "BELIEVABILITY", "PLAUSIBILITY", "COLLATERAL"
            )
            val steps = listOf(
                "ANALYZING SITUATION...",
                "CALCULATING DAMAGE...",
                "CHECKING YOUR STORY...",
                "SEARCHING THE EXCUSE DATABASE...",
                "YOU'RE PROBABLY FINE."
            )

            val apiDeferred = async {
                try {
                    repository.generateExcuses(
                        situation = situation,
                        category = category,
                        recipient = recipient,
                        severityLevel = severity,
                        vibe = vibe
                    )
                } catch (e: Exception) {
                    null
                }
            }

            var kwIdx = 0
            for (step in steps) {
                _scannerStepText.value = step
                for (i in 0..2) {
                    _scannerKeyword.value = keywords[kwIdx % keywords.size]
                    kwIdx++
                    delay(120)
                }
            }

            val result = apiDeferred.await()
            _scannerStepText.value = "EXCUSE FOUND."
            _scannerKeyword.value = "VERIFIED"
            delay(500)

            _isGenerating.value = false
            if (result != null) {
                _currentResult.value = result
                selectedExcuseIndex.value = 0
                _currentScreen.value = Screen.RESULTS
            } else {
                _generationError.value = "Something went wrong while generating your excuse."
            }
        }
    }

    fun cancelOrRetryGeneration() {
        scannerJob?.cancel()
        _isGenerating.value = false
        _generationError.value = null
        if (situationInput.value.isNotBlank()) {
            startGeneration()
        } else {
            navigateTo(Screen.HOME)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun setAnimationIntensity(intensity: String) {
        userPreferences.setAnimationIntensity(intensity)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        userPreferences.setHapticsEnabled(enabled)
    }

    fun getSurvivalRate(): Int = userPreferences.getSurvivalRate()

    fun openHistoryItem(entity: ExcuseEntity) {
        val safeOption = ExcuseOption(
            type = ExcuseType.SAFE,
            text = entity.safeText,
            believability = entity.safeBelievability,
            risk = entity.safeRisk,
            drama = 15,
            effort = 10
        )
        val funnyOption = ExcuseOption(
            type = ExcuseType.FUNNY,
            text = entity.funnyText,
            believability = entity.funnyBelievability,
            risk = entity.funnyRisk,
            drama = 60,
            effort = 25
        )
        val unhingedOption = ExcuseOption(
            type = ExcuseType.UNHINGED,
            text = entity.unhingedText,
            believability = entity.unhingedBelievability,
            risk = entity.unhingedRisk,
            drama = 98,
            effort = 10
        )

        _currentResult.value = ExcuseGenerationResult(
            id = entity.id,
            situation = entity.situation,
            category = entity.category,
            recipient = entity.recipient,
            severityLevel = entity.severityLevel,
            vibe = entity.vibe,
            excuses = listOf(safeOption, funnyOption, unhingedOption),
            believabilityScore = entity.overallBelievability,
            riskScore = entity.overallRisk,
            dramaScore = entity.overallDrama,
            effortScore = entity.overallEffort,
            timestamp = entity.timestamp
        )
        selectedExcuseIndex.value = 0
        navigateTo(Screen.RESULTS)
    }
}
