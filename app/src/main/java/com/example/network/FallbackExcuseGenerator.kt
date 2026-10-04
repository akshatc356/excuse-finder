package com.example.network

import com.example.data.model.DoctorCheckupResult
import com.example.data.model.ExcuseOption
import com.example.data.model.ExcuseType

object FallbackExcuseGenerator {

    fun generateExcuses(
        situation: String,
        category: String,
        recipient: String,
        severityLevel: Int,
        vibe: String
    ): GenerationAiResponse {
        val raw = situation.trim().trimEnd('.', '!', '?')
        val lower = raw.lowercase()

        // Extract cleaned essence of the user's input
        val coreAction = cleanActionPhrase(raw)

        val isBoss = recipient.equals("Boss", ignoreCase = true) || lower.contains("boss") || lower.contains("manager")
        val isParents = recipient.equals("Parents", ignoreCase = true) || lower.contains("mom") || lower.contains("dad") || lower.contains("parent")
        val isTeacher = recipient.equals("Teacher", ignoreCase = true) || lower.contains("professor") || lower.contains("teacher")
        val isPartner = recipient.equals("Partner", ignoreCase = true) || lower.contains("girlfriend") || lower.contains("boyfriend") || lower.contains("partner")
        val isFriend = recipient.equals("Friend", ignoreCase = true)

        val safeText: String
        val funnyText: String
        val unhingedText: String

        when {
            // Grades, Marks, Exam, Score, Failed, Report Card
            lower.contains("mark") || lower.contains("grade") || lower.contains("exam") ||
            lower.contains("test") || lower.contains("score") || lower.contains("failed") ||
            lower.contains("result") || lower.contains("report card") -> {
                safeText = when {
                    isParents -> "Mom & Dad, I wanted to discuss my recent marks with complete honesty. The curriculum was much more demanding than I anticipated and I fell behind, but I’ve already spoken with my instructors, set up tutoring sessions, and mapped out a daily study schedule to fix this immediately."
                    isTeacher -> "Dear Professor, I reviewed my exam results and take full accountability for falling short of expectations. I would appreciate 15 minutes during office hours to review the concepts I struggled with and understand available remediation options."
                    else -> "Regarding the test results: they definitely didn't reflect my full preparation due to a rough exam week, but I've already adjusted my study strategy and am on top of the next assessments."
                }
                funnyText = when {
                    isParents -> "Before you look at the report card, remember that Albert Einstein also had unconventional academic milestones. Consider these marks a suspenseful prologue to my triumphant second-semester comeback!"
                    else -> "My test paper and I had a fundamental philosophical disagreement on multiple-choice reality. The good news is, my academic trajectory can only head vertically upwards."
                }
                unhingedText = "A localized software glitch at the grading portal clearly transposed my test score with a random integer generator. I am formally protesting this statistical injustice."
            }

            // Late, Delayed, Traffic, Overslept, Alarm
            lower.contains("late") || lower.contains("delay") || lower.contains("traffic") ||
            lower.contains("alarm") || lower.contains("overslept") || lower.contains("behind schedule") ||
            lower.contains("stuck") -> {
                safeText = when {
                    isBoss -> "Good morning. Please accept my sincere apologies for running behind schedule. An unavoidable transit interruption delayed my departure, but I am actively en route, keeping an eye on urgent pings, and will stay late today to cover all deliverables."
                    isTeacher -> "Apologies for the delay in arriving to class today, Professor. A transit delay held me up, but I am heading in quietly and will borrow notes from a peer to ensure I don't miss the core lecture material."
                    isPartner -> "I am so sorry for running late, babe! My departure got derailed by an unexpected delay at home, but I am hurrying over safely now and will make it up to you tonight."
                    else -> "So sorry for the delay! Hit an unexpected bottleneck on the way, but sprinting over right now and should be there in a few minutes!"
                }
                funnyText = "My alarm clock executed its morning duties flawlessly, but my subconscious staged a coup and declared five more minutes of executive immunity. Sprinting now with extreme remorse!"
                unhingedText = "A minor temporal anomaly in my apartment caused time to flow at 3x standard velocity between my bed and front door. Physics was tested, but I am on my way."
            }

            // Forgot to Reply, Ghosting, Text, Message, Call
            lower.contains("reply") || lower.contains("ghost") || lower.contains("text") ||
            lower.contains("message") || lower.contains("call") || lower.contains("unread") -> {
                safeText = when {
                    isPartner -> "I am so sorry for leaving you on read! I opened your text in the middle of a chaotic work scramble, meant to reply with my full attention, and genuinely thought I hit send. Calling you right now!"
                    isBoss -> "Apologies for the delayed response to your message. I was occupied on a critical deadline earlier, but I have thoroughly reviewed your note and am taking action on this immediately."
                    else -> "So sorry for the radio silence! I saw your message right as things blew up on my end and was convinced I had drafted a response. How can we get this sorted right now?"
                }
                funnyText = "I mentally composed an eloquent, award-winning response the exact second your text popped up. Apparently my telepathic Bluetooth failed to sync with your phone. Forgive me!"
                unhingedText = "My phone entered a temporary spiritual retreat to teach me digital detachment. Its ascetic era has ended and I have reclaimed consciousness."
            }

            // Deadline, Assignment, Project, Homework, Submission
            lower.contains("deadline") || lower.contains("assignment") || lower.contains("project") ||
            lower.contains("homework") || lower.contains("submit") || lower.contains("deliverable") -> {
                safeText = when {
                    isBoss -> "I wanted to provide an update regarding the project deliverable. The primary implementation is done, and I am conducting rigorous QA and polish to deliver the highest quality output before sharing the final link shortly."
                    isTeacher -> "Dear Professor, I am finalizing the submission for the assignment. I ran into a brief technical formatting issue during export, but the core work is complete and I will submit it momentarily."
                    else -> "I'm wrapping up the final review of the project right now. Ran into a slight hitch during final compilation, but sending over the polished file in just a few minutes."
                }
                funnyText = "The document decided to experience a dramatic existential crisis right at 99% export. After some firm digital counseling, it has agreed to finalize."
                unhingedText = "Mercury entered retrograde right across my hard drive cache. The document fought valiantly in the cloud realm and is re-entering our reality right now."
            }

            // Party, Event, Cancel, Decline, Don't want to attend
            lower.contains("party") || lower.contains("event") || lower.contains("attend") ||
            lower.contains("cancel") || lower.contains("don't want") || lower.contains("skip") -> {
                safeText = when {
                    isBoss -> "Thank you so much for the invitation to the team event. Regrettably, I have a pre-existing family commitment scheduled for this evening that I cannot move, but I hope everyone has a fantastic time."
                    else -> "I really wanted to celebrate with you tonight, but an urgent personal matter came up that requires my presence at home. I feel terrible missing it, but let's grab lunch or coffee this week to catch up!"
                }
                funnyText = "My social battery hit 1% critical power and activated an emergency hibernation shutdown. Celebrating you in spirit from beneath three weighted blankets!"
                unhingedText = "I made direct eye contact with my couch and signed a legally binding, exclusive treaty with my sweatpants for the remainder of the evening."
            }

            // Broken, Damaged, Spill, Accident, Ruined
            lower.contains("broke") || lower.contains("damaged") || lower.contains("spill") ||
            lower.contains("ruined") || lower.contains("lost") || lower.contains("destroyed") -> {
                safeText = "I want to be completely upfront and transparent: an accident occurred earlier and $coreAction. I take total responsibility for the mishap and will cover the repair or replacement immediately."
                funnyText = "The good news is that no living creatures were harmed. The slightly less good news is that gravity and inanimate objects had an aggressive dispute today."
                unhingedText = "It didn't break; it underwent a sudden, non-consensual evolution into modern deconstructivist sculpture."
            }

            // General / Custom / Any Situation
            else -> {
                val politeDetail = if (coreAction.isNotBlank()) coreAction else "what happened earlier"
                safeText = when {
                    isBoss -> "I wanted to address the situation regarding $politeDetail directly. An unforeseen obstacle complicated things on my end, but I have already initiated the necessary steps to resolve it and keep everything on course."
                    isParents -> "Mom & Dad, I want to talk openly about $politeDetail. Things didn't unfold the way I intended and I know you're disappointed, but I am actively handling it and taking full responsibility."
                    isTeacher -> "Professor, I am writing to formally communicate regarding $politeDetail. I take full responsibility for the situation and have taken immediate steps to rectify any impact on coursework."
                    isPartner -> "Hey, I wanted to apologize sincerely about $politeDetail. Things got chaotic on my end and I handled the situation poorly, but I'm making it right immediately."
                    else -> "I wanted to reach out regarding $politeDetail. Things got unexpectedly complicated on my end and I dropped the ball, but I am fixing it right now."
                }
                funnyText = "Look, regarding $politeDetail, things took an avant-garde detour today, but on the bright side, we've unlocked a memorable character development milestone!"
                unhingedText = "Chaos chose me as its grand champion today regarding $politeDetail. The simulation threw everything at me, but I am emerging victorious with zero regrets."
            }
        }

        // Metrics calibration
        val (safeBelievability, safeRisk) = when (severityLevel) {
            1 -> 95 to 8
            2 -> 88 to 16
            else -> 82 to 24
        }
        val (funnyBelievability, funnyRisk) = when (severityLevel) {
            1 -> 68 to 22
            2 -> 58 to 36
            else -> 48 to 48
        }
        val (unhingedBelievability, unhingedRisk) = 18 to 92

        return GenerationAiResponse(
            excuses = listOf(
                ExcuseOption(
                    type = ExcuseType.SAFE,
                    text = safeText,
                    believability = safeBelievability,
                    risk = safeRisk,
                    drama = 14,
                    effort = 20
                ),
                ExcuseOption(
                    type = ExcuseType.FUNNY,
                    text = funnyText,
                    believability = funnyBelievability,
                    risk = funnyRisk,
                    drama = 55,
                    effort = 28
                ),
                ExcuseOption(
                    type = ExcuseType.UNHINGED,
                    text = unhingedText,
                    believability = unhingedBelievability,
                    risk = unhingedRisk,
                    drama = 96,
                    effort = 12
                )
            ),
            overallBelievability = if (vibe == "Unhinged") 25 else if (vibe == "Funny") 62 else 90,
            overallRisk = if (severityLevel == 3) 50 else if (severityLevel == 2) 28 else 12,
            overallDrama = if (vibe == "Unhinged" || vibe == "Drama queen") 88 else 38,
            overallEffort = 20
        )
    }

    private fun cleanActionPhrase(input: String): String {
        var res = input.trim()
        val prefixesToRemove = listOf(
            "i need an excuse because ", "i need an excuse for ", "i need an excuse ",
            "i forgot to ", "i forgot ", "i'm ", "i am ", "i ", "my "
        )
        for (p in prefixesToRemove) {
            if (res.startsWith(p, ignoreCase = true)) {
                res = res.substring(p.length).trim()
            }
        }
        return res
    }

    fun diagnoseExcuse(original: String): DoctorCheckupResult {
        val clean = original.trim().trimEnd('.', '!', '?')
        val lower = clean.lowercase()

        val problem = when {
            lower.contains("mark") || lower.contains("grade") || lower.contains("exam") ->
                "Hiding academic grades or blaming the exam curve destroys trust. Authority figures look for upfront transparency, composure, and a concrete study schedule."
            lower.contains("traffic") || lower.contains("train") || lower.contains("bus") ->
                "Traffic is the most exhausted excuse on earth. It screams poor time management unless paired with an affirmative commitment to make up the lost time."
            lower.contains("alarm") || lower.contains("overslept") || lower.contains("sleep") ->
                "Admitting you slept in frames you as careless and disorganized. Never mention oversleeping—frame it as an unexpected domestic delay."
            lower.contains("internet") || lower.contains("wifi") || lower.contains("connection") ->
                "Blaming Wi-Fi is outdated. People expect cellular hotspots or instant notifications."
            lower.contains("sick") || lower.contains("stomach") || lower.contains("headache") ->
                "Vague physical ailments invite scrutiny and follow-up medical queries. Keep reasons structural or personal."
            else ->
                "Your excuse sounds defensive and reactive. Strong damage control focuses 20% on the issue and 80% on the immediate resolution."
        }

        val patched = when {
            lower.contains("mark") || lower.contains("grade") || lower.contains("exam") ->
                "I wanted to speak with you openly about my exam results. I fell short of my goals this term, but I've already scheduled office hours with my instructor to organize extra credit and tutoring."
            lower.contains("traffic") || lower.contains("late") || lower.contains("alarm") ->
                "Please accept my sincere apologies for the delay. An unavoidable transit interruption held me up, but I am en route now and will ensure everything is completely caught up today."
            else ->
                "I wanted to reach out and address this directly. An unexpected priority required my immediate attention earlier, but everything is back on schedule now and I'm handling the next steps."
        }

        return DoctorCheckupResult(
            originalExcuse = original,
            believability = 85,
            risk = 15,
            problem = problem,
            patchedVersion = patched,
            shorterVersion = "Apologies for the delay—sorted now and handling this immediately.",
            moreNaturalVersion = "Hey, got caught in an urgent scramble earlier, all good now and on this!"
        )
    }
}
