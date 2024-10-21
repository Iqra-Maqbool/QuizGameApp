package com.example.quizgame.fragments.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizgame.R
import com.example.quizgame.databinding.FragmentLoginBinding
import com.example.quizgame.ext.showToast
import com.example.quizgame.fragments.baseFragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val loginViewModel: LoginViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeLoginMessage()
        navigateIfAlreadyLoggedIn()
    }


    private fun setupListeners() {
        binding.apply {
            loginBtn.setOnClickListener {
                val email = binding.loginUsername.text.toString()
                val password = binding.loginPassword.text.toString()
                loginViewModel.handleLoginValidation(email, password)
            }
            signupHere.setOnClickListener {
                navigateToSignupFragment()
            }
        }
    }

    private fun observeLoginMessage() {
        loginViewModel.message.observe(viewLifecycleOwner) { status ->
            when (status) {
                getString(R.string.LoginSuccess) -> {
                    requireContext().showToast(status)
                    navigateToHomeFragment()
                }
                else -> {
                    requireContext().showToast(status)
                }
            }
        }
    }

    private fun navigateIfAlreadyLoggedIn() {
        if (loginViewModel.alreadyLoggedIn()) {
            navigateToHomeFragment()
        }
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun navigateToSignupFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

}
