package com.example.cs481final.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.cs481final.R
import com.google.firebase.firestore.FirebaseFirestore



class LogMealFrag : Fragment(R.layout.fragment_log_meal) {

    private lateinit var email: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            email = it.getString("email", "").toString()
        }
        view.findViewById<Button>(R.id.bLog).setOnClickListener{
            val calories = view.findViewById<EditText>(R.id.CalorieEnter).text.toString().toInt()
            val meal = view.findViewById<EditText>(R.id.mealEnter).text.toString()
            val food = view.findViewById<EditText>(R.id.foodEnter).text.toString()
            val log: MutableMap<String, Any> = HashMap()
            val db = FirebaseFirestore.getInstance()


            log["user"] = email
            log["food"] = food
            log["meal"] = meal
            log["calories"]= calories
            db.collection("logMeal")
                .add(log)
                .addOnSuccessListener { Log.d("dbdirebase", "save, ${log}") }
                .addOnFailureListener{
                    Log.d("dbfirebase Failed", "${log}")
                }
            db.collection("logMeal")
                .get()
                .addOnCompleteListener{
                    val result: StringBuffer = StringBuffer()
                    if (it.isSuccessful) {
                        for (document in it.result!!) {
                            Log.d(
                                "dbfirebase", "retrieve: " +
                                        "${document.data.getValue("calories")} " +
                                        "${document.data.getValue("food")} " +
                                        "${document.data.getValue("meal")} "
                            )
                        }


                    }
                }
        }

        view.findViewById<Button>(R.id.button2).setOnClickListener {
            fetchAndDisplayLogs(view)
        }


    }
    private fun fetchAndDisplayLogs(view: View) {
        val db = FirebaseFirestore.getInstance()
        val logTextView: TextView = view.findViewById(R.id.textView3)
        logTextView.text = "" // Clear previous log text

        db.collection("logMeal")
            .whereEqualTo("user", email) // Filter logs by user's email
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val calories = document.getLong("calories") ?: 0
                    val food = document.getString("food") ?: ""
                    val meal = document.getString("meal") ?: ""
                    val logText = "Meal: $meal, Food: $food, Calories: $calories\n"
                    logTextView.append(logText)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LogMealFrag", "Error fetching logs: $exception")
                // Handle failure
            }
    }
}