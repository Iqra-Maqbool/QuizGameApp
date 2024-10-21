package com.example.quizgame.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.quizgame.databinding.CategoryItemsBinding

class CategoryAdapter(private val listener: OnItemClickListenerCategory) :
    ListAdapter<CategoryModelClass, CategoryAdapter.MyCategoryViewHolder>(CategoryDiffCallback()) {
    inner class MyCategoryViewHolder(private val binding: CategoryItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryModelClass) {
            binding.apply {
                categoryImg.setImageResource(category.categoryImage)
                NameOfSubj.text = category.categoryText

                itemView.setOnClickListener {
                    listener.onItemClick(category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryViewHolder {
        val binding =
            CategoryItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyCategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)

    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryModelClass>() {
    override fun areItemsTheSame(
        oldItem: CategoryModelClass,
        newItem: CategoryModelClass
    ): Boolean {
        return oldItem.categoryText == newItem.categoryText
    }

    override fun areContentsTheSame(
        oldItem: CategoryModelClass,
        newItem: CategoryModelClass
    ): Boolean {
        return oldItem == newItem
    }
}

interface OnItemClickListenerCategory {
    fun onItemClick(category: CategoryModelClass)
}
