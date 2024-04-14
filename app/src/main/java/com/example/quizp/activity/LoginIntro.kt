package  com.example.quizp.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.quizp.adapter.LoginIntroAdapter
import com.example.quizp.R


class LoginIntro : AppCompatActivity() {
    private lateinit var adapter: LoginIntroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = LoginIntroAdapter(this)
        adapter.checkCurrentUser()
        setContentView(R.layout.activity_login_intro)

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            adapter.redirectToLogin()
        }
    }
}

