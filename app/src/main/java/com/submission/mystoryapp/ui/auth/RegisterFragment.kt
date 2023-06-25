package com.submission.mystoryapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.submission.mystoryapp.R
import com.submission.mystoryapp.databinding.FragmentRegisterBinding
import com.submission.mystoryapp.di.ViewModelFactory
import com.submission.mystoryapp.ui.customview.MyEditText
import com.submission.mystoryapp.ui.viewmodel.AuthViewModel
import com.submission.mystoryapp.utils.Result

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding
    private lateinit var authViewModel: AuthViewModel

    private lateinit var regName: MyEditText
    private lateinit var regEmail: MyEditText
    private lateinit var regPass: MyEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        regName = binding!!.edRegisterName
        regEmail = binding!!.edRegisterEmail
        regPass = binding!!.edRegisterPassword

        binding?.signIn?.setOnClickListener {
            setupFragment()
        }

        binding?.root?.post {
            playAnimation()
        }

        setupModel()
        setupAction()
    }

    private fun setupAction() {
        binding?.apply {
            btnRegister.setOnClickListener {
                if (edRegisterName.error == null
                    && edRegisterName.text!!.isNotEmpty()
                    && edRegisterEmail.error == null
                    && edRegisterEmail.text!!.isNotEmpty()
                    && edRegisterPassword.error == null
                    && edRegisterPassword.text!!.isNotEmpty()
                ) {
                    val name = regName.text.toString().trim()
                    val email = regEmail.text.toString().trim()
                    val password = regPass.text.toString().trim()

                    authViewModel.userSignup(name, email, password).observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is Result.Loading -> showLoading(true)
                            is Result.Success -> {
                                Toast.makeText(requireContext(), R.string.success_register, Toast.LENGTH_SHORT).show()
                                Handler(Looper.getMainLooper()).postDelayed({
                                    showLoading(false)
                                }, 1000)
                                setupFragment()
                                Log.d("OnSuccessRegister: ", "response: ${response.data}")
                            }
                            is Result.Error -> {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    showLoading(false)
                                }, 1000)
                                val errorMessage = response.error
                                when  {
                                    errorMessage.contains("Email is already taken") -> Toast.makeText(requireContext(), R.string.error_register_by_email, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("\\\"name\\\" is not allowed to be empty") -> Toast.makeText(requireContext(), R.string.error_by_empty_name, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("\\\"email\\\" must be a valid email") -> Toast.makeText(requireContext(), R.string.error_by_invalid_email, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("\\\"email\\\" is not allowed to be empty") -> Toast.makeText(requireContext(), R.string.error_by_empty_email, Toast.LENGTH_SHORT).show()
                                    errorMessage.contains("\\\"password\\\" is not allowed to be empty") -> Toast.makeText(requireContext(), R.string.error_by_empty_password, Toast.LENGTH_SHORT).show()
                                    else -> Toast.makeText(requireContext(), R.string.error_register, Toast.LENGTH_SHORT).show()
                                }
                                Log.d("OnErrorRegister: ", "response: ${response.error}")
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
        }
    }

    @Suppress("DEPRECATION")
    private fun setupFragment() {
        val loginFragment = LoginFragment()
        fragmentManager?.beginTransaction()
            ?.replace(R.id.frame_container, loginFragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            edRegisterName.isEnabled = !isLoading
            edRegisterEmail.isEnabled = !isLoading
            edRegisterPassword.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
            progressBar.isVisible = isLoading
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.ivLogoRegister, View.TRANSLATION_X, -35f, 35f).apply {
            duration = 7000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val register = ObjectAnimator.ofFloat(binding?.tvRegister, View.ALPHA, 1f).setDuration(700)
        val email = ObjectAnimator.ofFloat(binding?.edRegisterEmail, View.ALPHA, 1f).setDuration(700)
        val name = ObjectAnimator.ofFloat(binding?.edRegisterName, View.ALPHA, 1f).setDuration(700)
        val pass = ObjectAnimator.ofFloat(binding?.edRegisterPassword, View.ALPHA, 1f).setDuration(700)
        val apply = ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).setDuration(700)
        val ask = ObjectAnimator.ofFloat(binding?.askSignIn, View.ALPHA, 1f).setDuration(700)
        val signin = ObjectAnimator.ofFloat(binding?.signIn, View.ALPHA, 1f).setDuration(700)

        val together = AnimatorSet().apply {
            playTogether(email, name, pass)
        }

        AnimatorSet().apply {
            playSequentially(
                register, apply, ask, signin, together
            )
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}