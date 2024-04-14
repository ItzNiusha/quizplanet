package com.example.quizp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizp.adapter.QuizListAdapter
import com.example.quizp.model.QuizModel
import com.example.quizp.R
import com.example.quizp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(), QuizListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var quizModelList: MutableList<QuizModel>
    private lateinit var adapter: QuizListAdapter
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var btnProfile: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnProfile = findViewById(R.id.btnProfile)

        btnProfile.setOnClickListener {
            // Handle the click event here
            openProfileActivity()
        }


        // Enable Firebase persistence before any other usage of FirebaseDatabase instance
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        quizModelList = mutableListOf()
        firebaseRef = FirebaseDatabase.getInstance().reference.child("quizzes")

        setupRecyclerView()
        fetchDataFromFirebase()

        val btnGoToLogin = binding.btnGoToLogin
        btnGoToLogin.setOnClickListener {
            signOut()
        }
    }

    private fun openProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizModelList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchDataFromFirebase() {
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        quizModel?.let {
                            quizModelList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()

                    // Start QuizActivity after data is loaded
//                    startQuizActivity()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if data retrieval fails
            }
        })
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Successfully signed out", Toast.LENGTH_SHORT).show()

        // Redirect to the login activity after signing out
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startQuizActivity() {
        // Start QuizActivity only if there is at least one quiz
        if (quizModelList.isNotEmpty()) {
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            intent.putExtra("questionModelList", ArrayList(quizModelList[0].questionList))
            intent.putExtra("time", quizModelList[0].time)
            startActivity(intent)
        } else {
            // Handle the case where no quizzes are available
            Toast.makeText(this, "No quizzes available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(position: Int) {
        val clickedItem = quizModelList[position]
        val intent = Intent(this@MainActivity, QuizActivity::class.java)
        intent.putExtra("questionModelList", ArrayList(clickedItem.questionList))
        intent.putExtra("time", clickedItem.time)
        startActivity(intent)
    }


}