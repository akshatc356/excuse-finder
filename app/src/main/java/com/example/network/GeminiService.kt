package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.DoctorCheckupResult
import com.example.data.model.ExcuseOption
import com.example.data.model.ExcuseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateExcuses(
        situation: String,
        category: String,
        recipient: String,
        severityLevel: Int,
        vibe: String,
        apiKeyOverride: String? = null,
        modelOverride: String? = null
    ): GenerationAiResponse = withContext(Dispatchers.IO) {
        val apiKey = when {
            !apiKeyOverride.isNullOrBlank() -> apiKeyOverride.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY.trim()
            else -> ""
        }

        if (apiKey.isBlank()) {
            Log.d("GeminiService", "Using contextual excuse generation engine (No API key provided)")
            return@withContext FallbackExcuseGenerator.generateExcuses(
                situation, category, recipient, severityLevel, vibe
            )
        }

        val primaryModel = if (!modelOverride.isNullOrBlank()) modelOverride.trim() else "gemini-3.5-flash"
        val modelsToTry = listOf(primaryModel, "gemini-3.1-pro-preview", "gemini-3.5-flash", "gemini-2.5-flash").distinct()

        val severityDesc = when (severityLevel) {
            1 -> "Level 1: NOT BAD (mild delay or minor social friction)"
            2 -> "Level 2: UH OH (noticeable blunder, upset recipient, needs solid explanation)"
            else -> "Level 3: I'M COOKED (major crisis, high stakes, high consequences, maximum damage control needed)"
        }

        val systemPrompt = """
            You are EXCUSE FINDER: a razor-sharp, hilarious, yet deeply accurate AI damage-control engine.
            Tagline: "Because 'I forgot' isn't going to save you."
            
            USER INPUT DETAILS:
            - EXACT SITUATION: "$situation"
            - RECIPIENT: "$recipient"
            - CATEGORY: "$category"
            - SEVERITY LEVEL: $severityDesc
            - DESIRED VIBE: "$vibe"
            
            STRICT ACCURACY & RELEVANCE RULES:
            1. Every excuse MUST be directly and explicitly about the exact situation described ("$situation"). Do NOT output unrelated generic excuses or vague filler.
            2. Directly reference specific details, nouns, actions, or implications mentioned in their situation.
            3. Tailor the tone, address, and politeness strictly to the recipient ("$recipient"). E.g., if talking to Boss, keep professional boundaries; if talking to Parents, address appropriately; if talking to Partner, address relationship intimacy; if talking to Teacher, address academic context.
            4. Never invent irrelevant fake scenarios (e.g. don't mention flat tires if they said they forgot homework).
            5. Safety: NEVER generate excuses involving fake deaths, fake medical emergencies, hospitalizations, self-harm, or dangerous illegal activities.
            
            GENERATE 3 DISTINCT TIERS:
            1. "safe": Highly believable (80-98%), low risk (5-20%), realistic, polite, addresses the situation without sounding defensive.
            2. "funny": Clever, witty, self-deprecating or meme-worthy spin specifically on the user's situation. Believability (45-70%), risk (25-50%).
            3. "unhinged": Audacious, chaotic, hilarious confidence that directly riffs on the user's predicament. Believability (10-35%), risk (75-95%).
            
            Return ONLY raw JSON matching this exact structure:
            {
              "excuses": [
                {
                  "type": "safe",
                  "text": "The exact excuse text (1-2 punchy, natural sentences).",
                  "believability": 92,
                  "risk": 12,
                  "drama": 15,
                  "effort": 18
                },
                {
                  "type": "funny",
                  "text": "Witty, humorous excuse tailored directly to the situation.",
                  "believability": 62,
                  "risk": 38,
                  "drama": 55,
                  "effort": 25
                },
                {
                  "type": "unhinged",
                  "text": "Chaotic, hilarious audacity directly about the situation.",
                  "believability": 20,
                  "risk": 85,
                  "drama": 95,
                  "effort": 10
                }
              ],
              "overallBelievability": 85,
              "overallRisk": 24,
              "overallDrama": 50,
              "overallEffort": 18
            }
        """.trimIndent()

        val rootJson = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemPrompt) })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            val generationConfig = JSONObject().apply {
                put("temperature", 0.75)
                put("responseMimeType", "application/json")
            }
            put("generationConfig", generationConfig)
        }

        for (model in modelsToTry) {
            try {
                Log.d("GeminiService", "Calling Gemini model: $model")
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
                    .post(rootJson.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val parsed = parseExcusesJson(responseBody)
                    if (parsed != null && parsed.excuses.isNotEmpty()) {
                        Log.d("GeminiService", "Gemini succeeded using $model!")
                        return@withContext parsed
                    }
                } else {
                    Log.w("GeminiService", "Gemini $model failed HTTP ${response.code}: $responseBody")
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Error calling $model: ${e.message}")
            }
        }

        // Contextual fallback if all remote models fail
        FallbackExcuseGenerator.generateExcuses(
            situation, category, recipient, severityLevel, vibe
        )
    }

    suspend fun runExcuseDoctor(
        originalExcuse: String,
        apiKeyOverride: String? = null,
        modelOverride: String? = null
    ): DoctorCheckupResult = withContext(Dispatchers.IO) {
        val apiKey = when {
            !apiKeyOverride.isNullOrBlank() -> apiKeyOverride.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY.trim()
            else -> ""
        }

        if (apiKey.isBlank()) {
            return@withContext FallbackExcuseGenerator.diagnoseExcuse(originalExcuse)
        }

        val prompt = """
            You are EXCUSE DOCTOR: the elite forensic auditor of weak excuses.
            The user wants you to inspect this excuse: "$originalExcuse"
            
            ANALYZE:
            1. What is the fatal flaw or weakness? Why does it sound defensive, suspicious, robotic, or unbelievable?
            2. Re-engineer a bulletproof "patched" version that is 10x more convincing and takes mature control of the topic.
            3. Provide an ultra-short version (under 12 words).
            4. Provide a casual, natural version with zero corporate or robotic phrasing.
            
            Return JSON:
            {
              "believability": 70,
              "risk": 30,
              "problem": "1-2 sentences ruthlessly identifying the psychological weakness of this excuse.",
              "patchedVersion": "Iron-clad, professional or natural rewrite addressing the exact subject.",
              "shorterVersion": "Crisp punchy version under 12 words.",
              "moreNaturalVersion": "Effortless, casual message that sounds like a real human."
            }
        """.trimIndent()

        val rootJson = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            val generationConfig = JSONObject().apply {
                put("temperature", 0.7)
                put("responseMimeType", "application/json")
            }
            put("generationConfig", generationConfig)
        }

        val model = if (!modelOverride.isNullOrBlank()) modelOverride.trim() else "gemini-3.5-flash"
        val models = listOf(model, "gemini-3.1-pro-preview", "gemini-2.5-flash").distinct()

        for (m in models) {
            try {
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/$m:generateContent?key=$apiKey")
                    .post(rootJson.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val parsed = parseDoctorJson(body)
                    if (parsed != null) return@withContext parsed.copy(originalExcuse = originalExcuse)
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Doctor call error on $m: ${e.message}")
            }
        }

        FallbackExcuseGenerator.diagnoseExcuse(originalExcuse)
    }

    private fun extractJsonString(raw: String): String {
        val trimmed = raw.trim()
        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1)
        }
        return trimmed
            .replace("```json", "", ignoreCase = true)
            .replace("```", "")
            .trim()
    }

    private fun parseExcusesJson(geminiResponse: String): GenerationAiResponse? {
        return try {
            val json = JSONObject(geminiResponse)
            val candidates = json.optJSONArray("candidates") ?: return null
            val firstCandidate = candidates.optJSONObject(0) ?: return null
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            val rawText = parts.optJSONObject(0)?.optString("text") ?: return null

            val cleanJsonString = extractJsonString(rawText)
            val resultJson = JSONObject(cleanJsonString)
            val excusesArray = resultJson.optJSONArray("excuses") ?: return null

            val list = mutableListOf<ExcuseOption>()
            for (i in 0 until excusesArray.length()) {
                val item = excusesArray.getJSONObject(i)
                val typeStr = item.optString("type", "safe").lowercase()
                val type = when (typeStr) {
                    "safe" -> ExcuseType.SAFE
                    "funny" -> ExcuseType.FUNNY
                    "unhinged" -> ExcuseType.UNHINGED
                    else -> ExcuseType.SAFE
                }
                list.add(
                    ExcuseOption(
                        type = type,
                        text = item.optString("text"),
                        believability = item.optInt("believability", 75),
                        risk = item.optInt("risk", 25),
                        drama = item.optInt("drama", 30),
                        effort = item.optInt("effort", 20)
                    )
                )
            }

            GenerationAiResponse(
                excuses = list,
                overallBelievability = resultJson.optInt("overallBelievability", 85),
                overallRisk = resultJson.optInt("overallRisk", 20),
                overallDrama = resultJson.optInt("overallDrama", 50),
                overallEffort = resultJson.optInt("overallEffort", 15)
            )
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to parse excuses: ${e.message}")
            null
        }
    }

    private fun parseDoctorJson(geminiResponse: String): DoctorCheckupResult? {
        return try {
            val json = JSONObject(geminiResponse)
            val candidates = json.optJSONArray("candidates") ?: return null
            val rawText = candidates.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: return null

            val cleanJson = extractJsonString(rawText)
            val parsed = JSONObject(cleanJson)
            DoctorCheckupResult(
                originalExcuse = "",
                believability = parsed.optInt("believability", 65),
                risk = parsed.optInt("risk", 35),
                problem = parsed.optString("problem", "Lacks specificity and feels reactive."),
                patchedVersion = parsed.optString("patchedVersion", ""),
                shorterVersion = parsed.optString("shorterVersion"),
                moreNaturalVersion = parsed.optString("moreNaturalVersion")
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class GenerationAiResponse(
    val excuses: List<ExcuseOption>,
    val overallBelievability: Int,
    val overallRisk: Int,
    val overallDrama: Int,
    val overallEffort: Int
)
