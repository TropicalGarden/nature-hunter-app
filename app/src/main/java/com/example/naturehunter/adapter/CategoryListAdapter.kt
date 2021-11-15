package com.example.naturehunter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.naturehunter.databinding.CategoryListItemBinding
import com.example.naturehunter.model.TypeItem
import com.example.naturehunter.ui.SelectCategoryFragmentDirections

class CategoryListAdapter(
    private val navController: NavController
) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    private val asyncDiffer = AsyncListDiffer(this, DiffCallback)

    companion object DiffCallback : DiffUtil.ItemCallback<TypeItem>() {

        override fun areItemsTheSame(
            oldItem: TypeItem,
            newItem: TypeItem
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: TypeItem,
            newItem: TypeItem
        ): Boolean {
            return oldItem.items == newItem.items
        }
    }

    class CategoryViewHolder(private var binding: CategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(typeItem: TypeItem) {
            binding.typeItem = typeItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            CategoryListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val typeItem = asyncDiffer.currentList[position]
        holder.itemView.setOnClickListener {
            navController.navigate(
                SelectCategoryFragmentDirections.actionSelectCategoryFragmentToItemListFragment(
                    typeItem.type
                )
            )
        }
        holder.bind(typeItem)
    }

    fun submitTypeItems(typeItems: List<TypeItem>) {
        asyncDiffer.submitList(typeItems)
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }
}