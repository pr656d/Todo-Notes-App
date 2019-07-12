package com.premp.todonotesapp.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.premp.todonotesapp.R
import com.premp.todonotesapp.utils.AppConstant
import com.premp.todonotesapp.utils.PrefConstant

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextFullName: EditText
    private lateinit var editTextUserName: EditText
    private lateinit var buttonLogin: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindViews()
        setupSharedPreference()

        val clickAction = OnClickListener {
            Log.d("LoginActivity: ", "Button Clicked")

            val fullName = editTextFullName.text.toString()
            val userName = editTextUserName.text.toString()

            if (fullName.isNotEmpty() && userName.isNotEmpty()) {
                val intent = Intent(this@LoginActivity, MyNotesActivity::class.java)
                intent.putExtra(AppConstant.FULL_NAME, fullName)
                startActivity(intent)
                //Login successful
                saveLoginStatus()
                saveFullName(fullName)
            } else {
                Toast.makeText(this@LoginActivity, "Full name and User name can't be empty",
                        Toast.LENGTH_SHORT).show()
            }
        }

        buttonLogin.setOnClickListener(clickAction)
    }

    private fun bindViews() {
        buttonLogin = findViewById(R.id.buttonLogin)
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextUserName = findViewById(R.id.editTextUserName)
    }

    private fun saveFullName(fullName: String) {
        editor = sharedPreferences.edit()
        editor.putString(PrefConstant.FULL_NAME, fullName)
        editor.apply()
    }

    private fun saveLoginStatus() {
        editor = sharedPreferences.edit()
        editor.putBoolean(PrefConstant.IS_LOGGED_IN, true)
        editor.apply()
    }

    private fun setupSharedPreference() {
        sharedPreferences = getSharedPreferences(PrefConstant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}
