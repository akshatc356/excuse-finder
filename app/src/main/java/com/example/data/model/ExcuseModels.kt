package com.example.data.model

data class ExcuseOption(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: ExcuseType,
    val text: String,
    val believability: Int,
    val risk: Int,
    val drama: Int = 20,
    val effort: Int = 15
)

enum class ExcuseType(val label: String, val emoji: String) {
    SAFE("SAFE", "🟢"),
    FUNNY("FUNNY", "🟡"),
    UNHINGED("UNHINGED", "🔴"),
    PATCHED("PATCHED", "🩺")
}

data class ExcuseGenerationResult(
    val id: Long = 0,
    val situation: String,
    val category: String,
    val recipient: String,
    val severityLevel: Int, // 1: NOT BAD, 2: UH OH, 3: I'M COOKED
    val vibe: String,
    val excuses: List<ExcuseOption>,
    val believabilityScore: Int,
    val riskScore: Int,
    val dramaScore: Int,
    val effortScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class DoctorCheckupResult(
    val originalExcuse: String,
    val believability: Int,
    val risk: Int,
    val problem: String,
    val patchedVersion: String,
    val shorterVersion: String? = null,
    val moreNaturalVersion: String? = null
)

data class BattleItem(
    val id: String,
    val scenario: String,
    val optionA: String,
    val optionB: String,
    val votesA: Int,
    val votesB: Int,
    val userVoted: String? = null, // "A" or "B" or null
    val commentary: String
) {
    val situation: String get() = scenario
    val excuseA: String get() = optionA
    val excuseB: String get() = optionB
}

enum class Category(val title: String, val iconEmoji: String, val samplePrompt: String) {
    COLLEGE("College", "🎓", "I didn't attend the morning class and professor noticed."),
    WORK("Work", "💼", "I am late for the morning standup with my boss."),
    PARENTS("Parents", "🏠", "I came home 3 hours after curfew."),
    FRIENDS("Friends", "🍻", "I forgot to reply to my friend for 3 days."),
    RELATIONSHIP("Relationship", "💔", "I forgot our dinner date plans."),
    MONEY("Money", "💸", "I need more time to return the borrowed money."),
    SOCIAL("Social", "🎉", "I don't want to attend the party tonight."),
    LATE("Late", "⏰", "I'm 2 hours late because of traffic."),
    MESSAGES("Messages", "💬", "I left their text on read since yesterday.");

    val displayName: String get() = title
}

enum class Recipient(val title: String, val iconEmoji: String) {
    FRIEND("Friend", "🤝"),
    PARENTS("Parents", "👨‍👩‍👧"),
    TEACHER("Teacher", "🧑‍🏫"),
    BOSS("Boss", "👔"),
    PARTNER("Partner", "❤️"),
    COWORKER("Coworker", "💼"),
    SOMEONE_ELSE("Someone else", "👤")
}

enum class Vibe(val title: String, val tag: String) {
    BELIEVABLE("Believable", "🎯"),
    FUNNY("Funny", "😂"),
    PROFESSIONAL("Professional", "📎"),
    INNOCENT("Innocent", "🥺"),
    DRAMATIC("Dramatic", "🎭"),
    UNHINGED("Unhinged", "🔥")
}
