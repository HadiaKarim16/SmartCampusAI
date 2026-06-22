package com.smartcampus.ai.data.remote

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AiService - Handles AI Study Assistant API calls
 * Currently uses mock responses. Replace BASE_URL and API_KEY to connect to OpenAI/Gemini.
 */
@Singleton
class AiService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    companion object {
        // 🔧 REPLACE with your actual API endpoint and key
        private const val BASE_URL = "https://api.openai.com/v1/chat/completions"
        private const val API_KEY = "YOUR_API_KEY_HERE"

        private val STUDY_TIPS = listOf(
            "Use the Pomodoro Technique: 25 minutes of focused study followed by a 5-minute break. This helps maintain concentration and reduces mental fatigue.",
            "Active recall is more effective than re-reading. Test yourself on material instead of passively reviewing notes.",
            "Spaced repetition: Review material at increasing intervals (1 day, 3 days, 1 week) to move it into long-term memory.",
            "Teach concepts to someone else or explain them aloud — the Feynman Technique reveals gaps in your understanding.",
            "Minimize distractions: Use app blockers and keep your phone in another room during study sessions.",
            "Sleep is essential for memory consolidation. A well-rested brain retains 40% more information.",
            "Study in the same place consistently — your brain associates the environment with focus.",
            "Break large tasks into smaller, manageable chunks to avoid overwhelm and procrastination.",
            "Use mind maps to visualize relationships between concepts for better retention.",
            "Hydrate and exercise regularly — physical health directly impacts cognitive performance."
        )

        private val MOCK_RESPONSES = mapOf(
            "summary" to "Here's a concise summary of the key concepts:\n\n📌 **Main Points:**\n• Core ideas are broken into digestible sections\n• Each concept builds on the previous one\n• Real-world applications are highlighted throughout\n\n🎯 **Key Takeaways:**\n• Understanding the fundamentals is critical before advancing\n• Practice problems reinforce theoretical knowledge\n• Regular revision helps cement long-term memory",
            "quiz" to "Here are 5 practice questions to test your understanding:\n\n**Q1.** What is the primary purpose of this concept?\na) Option A  b) Option B  c) Option C  d) Option D\n\n**Q2.** Which of the following best describes the process?\na) Option A  b) Option B  c) Option C  d) Option D\n\n**Q3.** How does this relate to real-world applications?\n*(Short answer)*\n\n**Q4.** Compare and contrast the two main approaches.\n*(Essay question)*\n\n**Q5.** Given a scenario, what would be the expected outcome?\na) Option A  b) Option B  c) Option C  d) Option D",
            "default" to "I'm your AI Study Assistant! 🎓 I can help you with:\n\n• **Concept explanations** — Break down complex topics\n• **Study summaries** — Condensed notes from any subject\n• **Quiz generation** — Practice questions for exam prep\n• **Study tips** — Personalized productivity advice\n• **Learning recommendations** — Resources for any topic\n\nAsk me anything about your coursework!"
        )
    }

    /**
     * Send a message to the AI and get a response.
     * Returns a mock response if no API key is configured.
     */
    suspend fun sendMessage(userMessage: String, subject: String = "General"): Result<String> {
        return withContext(Dispatchers.IO) {
            // Use mock if no real API key
            if (API_KEY == "YOUR_API_KEY_HERE") {
                return@withContext Result.success(getMockResponse(userMessage))
            }

            try {
                val requestBody = gson.toJson(
                    mapOf(
                        "model" to "gpt-3.5-turbo",
                        "messages" to listOf(
                            mapOf(
                                "role" to "system",
                                "content" to """You are SmartCampus AI, an intelligent academic assistant for university students.
                                |Subject context: $subject
                                |Help with study questions, summaries, quiz generation, and academic advice.
                                |Be concise, clear, and encouraging. Use markdown formatting.""".trimMargin()
                            ),
                            mapOf("role" to "user", "content" to userMessage)
                        ),
                        "max_tokens" to 500,
                        "temperature" to 0.7
                    )
                ).toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseJson = gson.fromJson(
                        response.body?.string() ?: "",
                        Map::class.java
                    )
                    val choices = responseJson["choices"] as? List<*>
                    val message = (choices?.firstOrNull() as? Map<*, *>)?.get("message") as? Map<*, *>
                    val content = message?.get("content") as? String ?: "No response received."
                    Result.success(content)
                } else {
                    Result.success(getMockResponse(userMessage))
                }
            } catch (e: Exception) {
                Result.success(getMockResponse(userMessage))
            }
        }
    }

    /**
     * Fetch a motivational quote for the dashboard
     */
    suspend fun getMotivationalQuote(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://api.quotable.io/random?tags=education,success,motivation")
                    .get()
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = gson.fromJson(response.body?.string() ?: "", Map::class.java)
                    val content = json["content"] as? String ?: ""
                    val author = json["author"] as? String ?: ""
                    Result.success("\"$content\" — $author")
                } else {
                    Result.success(getDefaultQuote())
                }
            } catch (e: Exception) {
                Result.success(getDefaultQuote())
            }
        }
    }

    private fun getMockResponse(input: String): String {
        val lower = input.lowercase()
        return when {
            lower.contains("summar") -> MOCK_RESPONSES["summary"]!!
            lower.contains("quiz") || lower.contains("test") || lower.contains("question") -> MOCK_RESPONSES["quiz"]!!
            lower.contains("tip") || lower.contains("advice") || lower.contains("help") ->
                "💡 **Study Tip:**\n\n${STUDY_TIPS.random()}"
            lower.contains("plan") || lower.contains("schedule") ->
                "📅 **Suggested Study Plan:**\n\n• **Morning (8-10 AM):** Review previous day's notes\n• **Late Morning (10-12 PM):** Study new material\n• **Afternoon (2-4 PM):** Practice problems\n• **Evening (6-7 PM):** Light review + flashcards\n• **Night:** Rest! Sleep consolidates memory 😴"
            else -> MOCK_RESPONSES["default"]!!
        }
    }

    private fun getDefaultQuote(): String {
        val quotes = listOf(
            "\"Education is the most powerful weapon you can use to change the world.\" — Nelson Mandela",
            "\"The expert in anything was once a beginner.\" — Helen Hayes",
            "\"Success is the sum of small efforts, repeated day in and day out.\" — Robert Collier",
            "\"Believe you can and you're halfway there.\" — Theodore Roosevelt",
            "\"It always seems impossible until it's done.\" — Nelson Mandela"
        )
        return quotes.random()
    }
}
