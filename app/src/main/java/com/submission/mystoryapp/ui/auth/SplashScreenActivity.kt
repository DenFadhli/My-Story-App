package com.submission.mystoryapp.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.submission.mystoryapp.databinding.ActivitySplashScreenBinding
import com.submission.mystoryapp.di.ViewModelFactory
import com.submission.mystoryapp.ui.main.MainActivity
import com.submission.mystoryapp.ui.viewmodel.AuthViewModel

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private val authViewModel: AuthViewModel by viewModels{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Init ViewModel
        checkLogin()
    }

    private fun checkLogin() {
        authViewModel.fetchUser().observe(this) { token ->
            if (token == null) {
                startActivity(
                    Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
                finish()
            }
        }
    }
}