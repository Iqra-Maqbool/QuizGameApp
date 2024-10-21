package com.example.quizgame.fragments.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizgame.R
import com.example.quizgame.ext.showToast
import com.example.quizgame.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupListeners()
        observeSignupMessage()
    }

    private fun setupListeners() {
        binding.apply {
            registerBtn.setOnClickListener {
                val username = signupUsername.text.toString()
                val email = signupEmail.text.toString()
                val password = signupPassword.text.toString()
                val cPassword = signupCPassword.text.toString()
                signupViewModel.handleSignupValidation(username, email, password, cPassword)
            }
            loginHere.setOnClickListener {
                navigateToLoginFragment()
            }
        }
    }

    private fun observeSignupMessage() {
        signupViewModel.message.observe(viewLifecycleOwner) { status ->
            when (status) {
                getString(R.string.SignupSuccess) -> {
                    requireContext().showToast(status)
                    navigateToHomeFragment()
                }
                else -> {
                    requireContext().showToast(status)
                }
            }
        }
    }

    private fun navigateToLoginFragment() {
        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_signupFragment_to_homeFragment)
    }
}
