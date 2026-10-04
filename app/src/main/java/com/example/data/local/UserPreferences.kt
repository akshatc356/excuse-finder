package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("excuse_finder_prefs", Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(prefs.getString("user_name", "AGENT MAVERICK") ?: "AGENT MAVERICK")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userBio = MutableStateFlow(
        prefs.getString("user_bio", "Professional ghoster & emergency alibi architect.") ?: "Professional ghoster & emergency alibi architect."
    )
    val userBio: StateFlow<String> = _userBio.asStateFlow()

    private val _userAvatar = MutableStateFlow(prefs.getString("user_avatar", "SMUG") ?: "SMUG")
    val userAvatar: StateFlow<String> = _userAvatar.asStateFlow()

    private val _defaultRecipient = MutableStateFlow(prefs.getString("default_recipient", "Friend") ?: "Friend")
    val defaultRecipient: StateFlow<String> = _defaultRecipient.asStateFlow()

    private val _defaultVibe = MutableStateFlow(prefs.getString("default_vibe", "Believable") ?: "Believable")
    val defaultVibe: StateFlow<String> = _defaultVibe.asStateFlow()

    private val _totalGenerated = MutableStateFlow(prefs.getInt("total_generated", 6))
    val totalGenerated: StateFlow<Int> = _totalGenerated.asStateFlow()

    private val _totalCopied = MutableStateFlow(prefs.getInt("total_copied", 4))
    val totalCopied: StateFlow<Int> = _totalCopied.asStateFlow()

    private val _mostUsedCategory = MutableStateFlow(prefs.getString("most_used_category", "Late") ?: "Late")
    val mostUsedCategory: StateFlow<String> = _mostUsedCategory.asStateFlow()

    private val _favoriteVibe = MutableStateFlow(prefs.getString("favorite_vibe", "Believable") ?: "Believable")
    val favoriteVibe: StateFlow<String> = _favoriteVibe.asStateFlow()

    private val _animationIntensity = MutableStateFlow(prefs.getString("anim_intensity", "TACTICAL") ?: "TACTICAL")
    val animationIntensity: StateFlow<String> = _animationIntensity.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean("haptics_enabled", true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.getString("custom_gemini_api_key", "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow(prefs.getString("selected_model", "gemini-3.5-flash") ?: "gemini-3.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    fun setUserName(name: String) {
        val sanitized = name.trim().ifBlank { "AGENT MAVERICK" }
        _userName.value = sanitized
        prefs.edit().putString("user_name", sanitized).apply()
    }

    fun setUserBio(bio: String) {
        val sanitized = bio.trim().ifBlank { "Damage control specialist." }
        _userBio.value = sanitized
        prefs.edit().putString("user_bio", sanitized).apply()
    }

    fun setUserAvatar(avatar: String) {
        _userAvatar.value = avatar
        prefs.edit().putString("user_avatar", avatar).apply()
    }

    fun setDefaultRecipient(recipient: String) {
        _defaultRecipient.value = recipient
        prefs.edit().putString("default_recipient", recipient).apply()
    }

    fun setDefaultVibe(vibe: String) {
        _defaultVibe.value = vibe
        prefs.edit().putString("default_vibe", vibe).apply()
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key.trim()
        prefs.edit().putString("custom_gemini_api_key", key.trim()).apply()
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
        prefs.edit().putString("selected_model", model).apply()
    }

    fun recordGeneration(category: String, vibe: String) {
        val newGen = _totalGenerated.value + 1
        _totalGenerated.value = newGen
        prefs.edit().putInt("total_generated", newGen).apply()

        val catCount = prefs.getInt("cat_$category", 0) + 1
        prefs.edit().putInt("cat_$category", catCount).apply()
        _mostUsedCategory.value = category
        prefs.edit().putString("most_used_category", category).apply()

        val vibeCount = prefs.getInt("vibe_$vibe", 0) + 1
        prefs.edit().putInt("vibe_$vibe", vibeCount).apply()
        _favoriteVibe.value = vibe
        prefs.edit().putString("favorite_vibe", vibe).apply()
    }

    fun recordCopy() {
        val newCopied = _totalCopied.value + 1
        _totalCopied.value = newCopied
        prefs.edit().putInt("total_copied", newCopied).apply()
    }

    fun setAnimationIntensity(intensity: String) {
        _animationIntensity.value = intensity
        prefs.edit().putString("anim_intensity", intensity).apply()
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
    }

    fun getSurvivalRate(): Int {
        val gen = _totalGenerated.value
        val cop = _totalCopied.value
        if (gen == 0) return 92
        val rate = 72 + ((cop * 25) / gen.coerceAtLeast(1))
        return rate.coerceIn(60, 99)
    }

    fun clearStats() {
        prefs.edit()
            .putInt("total_generated", 0)
            .putInt("total_copied", 0)
            .putString("most_used_category", "None")
            .putString("favorite_vibe", "Believable")
            .apply()
        _totalGenerated.value = 0
        _totalCopied.value = 0
        _mostUsedCategory.value = "None"
        _favoriteVibe.value = "Believable"
    }
}
