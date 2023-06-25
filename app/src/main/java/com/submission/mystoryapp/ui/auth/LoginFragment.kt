package com.submission.mystoryapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.submission.mystoryapp.R
import com.submission.mystoryapp.data.network.UserStory
import com.submission.mystoryapp.databinding.FragmentLoginBinding
import com.submission.mystoryapp.di.ViewModelFactory
import com.submission.mystoryapp.ui.main.MainActivity
import com.submission.mystoryapp.ui.viewmodel.AuthViewModel
import com.submission.mystoryapp.utils.Result

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.root?.post {
            playAnimation()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        setupModel()
        setupAction()
    }

    private fun setupAction() {
        binding?.apply {
            btnLogin.setOnClickListener {
                if (edLoginEmail.error == null
                    && edLoginEmail.text!!.isNotEmpty()
                    && edLoginPassword.error == null
                    && edLoginPassword.text!!.isNotEmpty()) {
                    val email = binding?.edLoginEmail?.text.toString().trim()
                    val password = binding?.edLoginPassword?.text.toString().trim()
                    authViewModel.userLogin(email, password).observe(viewLifecycleOwner) { response ->
                        when(response) {
                            is Result.Loading -> showLoading(true)
                            is Result.Success -> {
                                saveUserData(
                                    UserStory(
                                        response.data.loginResult?.token.toString(),
                                        response.data.loginResult?.name.toString(),
                                        true
                                    )
                                )
                                Handler(Looper.getMainLooper()).postDelayed({
                                    showLoading(false)
                                }, 1000)
                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                Log.d("OnSuccessLogin: ", "response: ${response.data}")
                            }
                            is Result.Error -> {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    showLoading(false)
                                }, 1000)
                                Log.d("OnErrorLogin: ", "response: ${response.error}")
                                val errorMessage = response.error
                                when {
                                    errorMessage.contains("Invalid password") -> Toast.makeText(requireContext(), R.string.error_login_by_password, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("User not found") -> Toast.makeText(requireContext(), R.string.error_login_user_not_found, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("\\\"email\\\" must be a valid email") -> Toast.makeText(requireContext(), R.string.error_by_invalid_email, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("\\\"email\\\" is not allowed to be empty") -> Toast.makeText(requireContext(), R.string.error_by_empty_email, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("\\\"password\\\" is not allowed to be empty") -> Toast.makeText(requireContext(), R.string.error_by_empty_password, Toast.LENGTH_SHORT).show()
                                    else -> Toast.makeText(requireContext(), R.string.error_login, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.check_validity_field,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            signUp.setOnClickListener{
                val registerFragment = RegisterFragment()
                @Suppress("DEPRECATION")
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_container, registerFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }


    private fun saveUserData(user: UserStory) {
        authViewModel.saveUser(user)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            edLoginEmail.isEnabled = !isLoading
            edLoginPassword.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading
            progressBar.isVisible = isLoading
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.ivLogoLogin, View.TRANSLATION_X, -35f, 35f).apply {
            duration = 7000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val welcome = ObjectAnimator.ofFloat(binding?.tvWelcome, View.ALPHA, 1f).setDuration(700)
        val email = ObjectAnimator.ofFloat(binding?.edLoginEmail, View.ALPHA, 1f).setDuration(700)
        val pass = ObjectAnimator.ofFloat(binding?.edLoginPassword, View.ALPHA, 1f).setDuration(700)
        val login = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(700)
        val ask = ObjectAnimator.ofFloat(binding?.askSignUp, View.ALPHA, 1f).setDuration(700)
        val signup = ObjectAnimator.ofFloat(binding?.signUp, View.ALPHA, 1f).setDuration(700)

        val together = AnimatorSet().apply {
            playTogether(email, pass)
        }

        AnimatorSet().apply {
            playSequentially(
                welcome, login, ask, signup, together
            )
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}