package com.example.quizgame.fragments.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizgame.databinding.FragmentHomeBinding
import com.example.quizgame.ext.showToast
import com.example.quizgame.fragments.baseFragment.BaseFragment


class HomeFragment :  BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), OnItemClickListenerCategory {

    private lateinit var categoryAdapter: CategoryAdapter
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onItemClick(category: CategoryModelClass) {
        val action = HomeFragmentDirections.actionHomeFragmentToQuizFragment(category.categoryImage, category.categoryText)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModelData()
        fetchInitialData()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(this)
        binding.categoryRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoryAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModelData() {
        homeViewModel.apply {
            checkList.observe(viewLifecycleOwner) { subject ->
                subject?.let {
                    categoryAdapter.submitList(it)
                }
            }

            username.observe(viewLifecycleOwner) { username ->
                username?.let {
                    binding.ShowName.text = it
                }
            }

            coins.observe(viewLifecycleOwner) { coins ->
                binding.CoinTextView.text = coins.toString()
            }

            message.observe(viewLifecycleOwner) { error ->
                error?.let {
                    requireContext().showToast(it)
                }
            }
        }
    }

    private fun fetchInitialData() {
        homeViewModel.apply {
            fetchUsername()
            fetchCoins()
            setRecyclerForCategoryList()
        }
    }
}
