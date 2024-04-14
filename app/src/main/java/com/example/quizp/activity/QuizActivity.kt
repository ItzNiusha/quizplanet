package com.example.quizp.activity

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizp.model.QuestionModel
import com.example.quizp.R
import com.example.quizp.databinding.ActivityQuizBinding
import com.example.quizp.databinding.ScoreDialogBinding

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuizBinding
    private var currentQuestionIndex = 0
    private var selectedAnswer = ""
    private var score = 0

    private lateinit var countdownTimer: CountDownTimer
    private lateinit var scoreDialog: AlertDialog

    private lateinit var questionModelList: List<QuestionModel>
    private var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)



        questionModelList =
            intent.getSerializableExtra("questionModelList") as? List<QuestionModel> ?: emptyList()
        time = intent.getStringExtra("time") ?: ""

        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

        startTimer()
        loadQuestions()
    }

    private fun startTimer() {
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text =
                    String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    private fun loadQuestions() {
        if (currentQuestionIndex < questionModelList.size) {
            val currentQuestion = questionModelList[currentQuestionIndex]

            binding.apply {
                questionIndicatorTextview.text =
                    "Question ${currentQuestionIndex + 1}/${questionModelList.size}"
                questionTextview.text = currentQuestion.question
                btn0.text = currentQuestion.options[0]
                btn1.text = currentQuestion.options[1]
                btn2.text = currentQuestion.options[2]
                btn3.text = currentQuestion.options[3]

                // Reset background color for buttons (assuming btn0, btn1, btn2, btn3 are your option buttons)
                btn0.setBackgroundColor(getColor(R.color.purple_200))
                btn1.setBackgroundColor(getColor(R.color.purple_200))
                btn2.setBackgroundColor(getColor(R.color.purple_200))
                btn3.setBackgroundColor(getColor(R.color.purple_200))
            }
        } else {
            finishQuiz()
        }
    }


    override fun onClick(view: View?) {


        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.purple_200))
            btn1.setBackgroundColor(getColor(R.color.purple_200))
            btn2.setBackgroundColor(getColor(R.color.purple_200))
            btn3.setBackgroundColor(getColor(R.color.purple_200))
        }

        if (view is Button) {
            val clickedBtn = view

            if (clickedBtn.id == R.id.next_btn) {
                // Next button is clicked
                if (selectedAnswer.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Please select an answer to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (selectedAnswer == questionModelList[currentQuestionIndex].correct) {
                    score++
                    Log.i("Score of quiz", score.toString())
                }
                currentQuestionIndex++
                loadQuestions()
            } else {
                // Options button is clicked
                selectedAnswer = clickedBtn.text.toString()
                clickedBtn.setBackgroundColor(getColor(R.color.orange))
            }
        }
    }


    private fun finishQuiz() {
        // Check if the Activity is finishing or has been destroyed
        if (!isFinishing) {
            val totalQuestions = questionModelList.size
            val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

            val dialogBinding = ScoreDialogBinding.inflate(layoutInflater)
            dialogBinding.apply {
                scoreProgressIndicator.progress = percentage
                scoreProgressText.text = "$percentage %"
                if (percentage > 60) {
                    scoreTitle.text = "Congrats! You have passed"
                    scoreTitle.setTextColor(Color.GREEN)
                } else {
                    scoreTitle.text = "Oops! You have failed"
                    scoreTitle.setTextColor(Color.GRAY)
                }
                scoreSubtitle.text = "$score out of $totalQuestions are correct"
                finishBtn.setOnClickListener {
                    finish()
                }
            }

            // Check again before showing the AlertDialog
            if (!isFinishing) {
                AlertDialog.Builder(this)
                    .setView(dialogBinding.root)
                    .setCancelable(false)
                    .show()
            }
        }
    }

}

