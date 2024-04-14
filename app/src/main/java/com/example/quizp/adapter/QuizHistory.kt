package com.example.quizp.adapter

import android.content.Context
import android.content.SharedPreferences

// QuizHistoryManager.kt
class QuizHistoryManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("quiz_history", Context.MODE_PRIVATE)

    fun saveLastQuiz(quizTitle: String, score: Int) {
        val editor = sharedPreferences.edit()
        editor.putString("last_quiz_title", quizTitle)
        editor.putInt("last_quiz_score", score)
        editor.apply()
    }

    fun getLastQuiz(): Pair<String?, Int> {
        val quizTitle = sharedPreferences.getString("last_quiz_title", null)
        val score = sharedPreferences.getInt("last_quiz_score", 0)
        return Pair(quizTitle, score)
    }
}
