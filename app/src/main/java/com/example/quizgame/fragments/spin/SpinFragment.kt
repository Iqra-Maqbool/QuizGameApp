package com.example.quizgame.fragments.spin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.quizgame.R
import com.example.quizgame.ext.showToast
import com.example.quizgame.databinding.FragmentSpinBinding

class SpinFragment : Fragment() {
    private lateinit var binding: FragmentSpinBinding
    private val spinViewModel: SpinViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSpinBinding.inflate(inflater, container, false)

        handleBackPress()
        observeViewModelData()
        setupSpinButton()
        fetchInitialData()
        return binding.root
    }


    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }


    private fun observeViewModelData() {
        spinViewModel.apply {
            username.observe(viewLifecycleOwner) { username ->
                username?.let { binding.ShowName.text = it }
            }

            coins.observe(viewLifecycleOwner) { coins ->
                binding.CoinTextView.text = coins.toString()
            }

            spinChances.observe(viewLifecycleOwner) { chances ->
                binding.SpinChances.text = chances.toString()
            }

            message.observe(viewLifecycleOwner) { error ->
                error?.let { requireContext().showToast(it) }
            }

            rotation.observe(viewLifecycleOwner) { rotation ->
                binding.spinImg.rotation = rotation
            }

            spinResult.observe(viewLifecycleOwner) { result ->
                showSpinResult(result)
            }
        }
    }


    private fun setupSpinButton() {
        binding.spinButton.setOnClickListener {
            handleSpinButtonClick()
        }
    }


    private fun handleSpinButtonClick() {
        binding.spinButton.isEnabled = false
        val chances = spinViewModel.spinChances.value ?: 0
        if (chances > 0) {
            spinViewModel.startSpin()
        } else {
            requireContext().showToast(getString(R.string.NO_SPIN_CHANCES_AVAILABLE))
            binding.spinButton.isEnabled = true
        }
    }


    private fun fetchInitialData() {
        spinViewModel.apply {
            fetchUsername()
            fetchCoins()
            fetchSpinChances()
        }
    }


    private fun showSpinResult(result: String) {
        requireContext().showToast(result)
        binding.spinButton.isEnabled = true
    }
}
