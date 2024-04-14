package com.example.quizp.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quizp.activity.LoginActivity
import com.example.quizp.databinding.ActivityProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var btnGoToMain: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Check if the user is logged in
        if (auth.currentUser != null) {
            val currentUser = auth.currentUser
            val userEmail = currentUser?.email
            binding.emailText.text = "Email: $userEmail"

            databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(currentUser!!.uid)
            loadUserProfile()

            // Find the button by its ID
            binding.btnGoToMain.setOnClickListener {
                openMainActivity()
            }

            // Button logic to delete account
            val deleteAccountButton = binding.deleteAccountButton
            deleteAccountButton.setOnClickListener {
                // Show a confirmation dialog before deleting the account
                showDeleteAccountConfirmationDialog()
            }
        } else {
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    private fun showDeleteAccountConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account? This action is irreversible.")

        builder.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
            // If user confirms, proceed with account deletion
            deleteAccount()
        }

        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            // If user cancels, do nothing or provide feedback
            // You can customize this part based on your app's UX
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun loadUserProfile() {
        databaseReference.child("scores").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var totalScore = 0
                var quizCount = 0

                for (quizSnapshot in dataSnapshot.children) {
                    val score = quizSnapshot.child("score").getValue(Int::class.java)
                    if (score != null) {
                        totalScore += score
                        quizCount++
                    }
                }

//                if (quizCount > 0) {
//                    val averageScore = totalScore.toFloat() / quizCount.toFloat()
//
//                    binding.apply {
//                        averageScoreText.text = String.format("Average Score: %.2f", averageScore)
//                        totalQuizzesText.text = String.format("Total Quizzes: %d", quizCount)
//                    }
//                } else {
//                    binding.averageScoreText.text = "No quizzes taken yet."
//                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                binding.averageScoreText.text = "Error loading data."
            }
        })
    }

    private fun deleteAccount() {
        val user = auth.currentUser

        // Check if the user is not null
        user?.let { currentUser ->
            // Re-authenticate the user
            val credential = EmailAuthProvider.getCredential(currentUser.email!!, "user_password")
            currentUser.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Re-authentication successful, proceed with account deletion
                        currentUser.delete()
                            .addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    // Account deleted successfully
                                    redirectToLogin()
                                } else {
                                    // If the deletion fails, display a message to the user
                                    val errorMessage = deleteTask.exception?.message ?: "Account deletion failed"
                                    showErrorDialog(errorMessage)
                                }
                            }
                    } else {
                        // Re-authentication failed, check for token expiration
                        if (reauthTask.exception is FirebaseAuthInvalidCredentialsException) {
                            // If re-authentication fails due to invalid credentials, prompt the user to log in again
                            redirectToLogin()
                        } else {
                            // Other re-authentication errors, display a message to the user
                            val errorMessage = reauthTask.exception?.message ?: "Re-authentication failed"
                            showErrorDialog(errorMessage)
                        }
                    }
                }
        } ?: run {
            showErrorDialog("User is null. Unable to delete account.")
        }
    }


    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
            // Handle the OK button click if needed
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}

