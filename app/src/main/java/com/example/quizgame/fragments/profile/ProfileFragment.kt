package com.example.quizgame.fragments.profile

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizgame.R
import com.example.quizgame.authentication.UserModelClass
import com.example.quizgame.databinding.FragmentProfileBinding
import com.example.quizgame.ext.showToast
import com.example.quizgame.fragments.baseFragment.BaseFragment
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment :BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButtonHandler()
        setupLogoutButton()
        observeViewModelData()
    }

    private fun setupBackButtonHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun setupLogoutButton() {
        binding.LogoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun observeViewModelData() {
        profileViewModel.apply {
            userData.observe(viewLifecycleOwner) { user ->
                user?.let {
                    displayUserData(user)
                }
            }

            message.observe(viewLifecycleOwner) { error ->
                error?.let {
                    requireContext().showToast(it)
                }
            }
            fetchUserData()
        }
    }

    private fun displayUserData(user: UserModelClass) {
        binding.apply {
            ShowNameTV.text = user.username
            ShowName.text = user.username
            ShowEmailTV.text = user.email
            ShowPasswordTV.text = user.password
        }
    }
}
