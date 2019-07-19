package com.premp.todonotesapp.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.premp.todonotesapp.R
import com.premp.todonotesapp.onboarding.OnBoardingActivity
import com.premp.todonotesapp.utils.PrefConstant

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupSharedPreference()
        checkLoginStatus()
        getFCMToken()
    }

    private fun getFCMToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("SplashActivity", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    Log.d("SplashActivity", token)
                    Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
                })

    }

    private fun setupSharedPreference() {
        sharedPreferences = getSharedPreferences(PrefConstant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean(PrefConstant.IS_LOGGED_IN, false)
        val isBoardingSuccess = sharedPreferences.getBoolean(PrefConstant.ON_BOARDED_SUCCESSFULLY, false)

        val intent =
                when {
                    // If logged in open My Notes Activity.
                    isLoggedIn -> Intent(this@SplashActivity, MyNotesActivity::class.java)

                    // If boarded successfully then Login Activity.
                    isBoardingSuccess -> Intent(this@SplashActivity, LoginActivity::class.java)

                    // If not boarded then Boarding Activity.
                    else -> Intent(this@SplashActivity, OnBoardingActivity::class.java)
                }

        startActivity(intent)
    }
}
