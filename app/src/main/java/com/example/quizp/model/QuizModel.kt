package com.example.quizp.model

import java.io.Serializable


data class QuizModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val time: String,
    val questionList: List<QuestionModel>
) : Serializable {
    constructor() : this("", "", "", "", emptyList())
}

data class QuestionModel(
    val question: String,
    val options: List<String>,
    val correct: String,
) : Serializable {
    constructor() : this("", emptyList(), "")
}
