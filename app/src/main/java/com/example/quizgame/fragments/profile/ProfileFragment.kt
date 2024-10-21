package com.example.quizgame.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizgame.R
import com.example.quizgame.authentication.UserModelClass
import com.example.quizgame.ext.showToast
import com.example.quizgame.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupBackButtonHandler()
        setupLogoutButton()
        observeViewModelData()
        return binding.root
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
