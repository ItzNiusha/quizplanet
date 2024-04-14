package com.example.quizp.adapter

import android.content.Context
import android.widget.Toast
import com.example.quizp.activity.Authentication
import com.example.quizp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

class RegisterAdapter(private val context: Context, private val onSuccess: () -> Unit) :
    Authentication() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: User? = null

    fun createUserWithEmailAndPassword(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(context, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(
                context,
                "Password and Confirm Password do not match",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!isValidEmailDomain(email)) {
            Toast.makeText(context, "Only Gmail and Yahoo emails are allowed", Toast.LENGTH_SHORT)
                .show()
            return
        }

        user = User().apply {
            this.email = email
            this.password = password
        }
        auth() // Call auth() only when all validations pass
    }

    private fun isValidEmailDomain(email: String): Boolean {
        val validDomains = listOf("gmail.com", "yahoo.com")
        val domain = email.split("@").lastOrNull()?.toLowerCase()
        return domain != null && validDomains.contains(domain)
    }


    override fun auth() {
        user?.let {
            firebaseAuth.createUserWithEmailAndPassword(it.email, user!!.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val databaseR = Firebase.database.reference
                        val userId = databaseR.child("users").push().key ?: ""
                        databaseR.child("users").child(userId).setValue(user)

                        Toast.makeText(
                            context,
                            "Welcome, Registration Successful",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        onSuccess.invoke() // Invoke the success callback
                    } else {
                        Toast.makeText(context, "Error Creating User", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
