package com.submission.mystoryapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.submission.mystoryapp.R
import com.submission.mystoryapp.databinding.ActivityAuthBinding
import com.submission.mystoryapp.di.ViewModelFactory
import com.submission.mystoryapp.ui.main.MainActivity
import com.submission.mystoryapp.ui.viewmodel.AuthViewModel

class AuthActivity : AppCompatActivity() {
    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.hide()

        @Suppress("DEPRECATION")
        window.insetsController?.hide(WindowInsets.Type.statusBars())

        setupModel()
        authViewModel.getUser().observe(this) { user ->
            if (user.isLogin){
                Log.d("Auth Check: ", "Have OnLogin")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else {
                Log.d("Auth Check: ", "Haven't OnLogin")
                setupView()
            }
        }
        setupModel()
    }

    private fun setupView() {
        val fragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)
        if (fragment !is LoginFragment) {
            Log.d("MyFlexibleFragment", "Fragment Name :" + LoginFragment::class.java.simpleName)
            fragmentManager
                .beginTransaction()
                .add(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }
}