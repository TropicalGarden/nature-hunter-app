package com.example.naturehunter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.naturehunter.constant.Type
import com.example.naturehunter.data.model.Item
import com.example.naturehunter.databinding.ItemListItemBinding
import com.example.naturehunter.ui.ItemListFragmentDirections

class ItemListAdapter(
    private val navController: NavController
) : ListAdapter<Item, ItemListAdapter.ItemViewHolder>(
    DiffCallback
) {

    class ItemViewHolder(private var binding: ItemListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.item = item
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(
            oldItem: Item,
            newItem: Item
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Item,
            newItem: Item
        ): Boolean {
            return oldItem.itemName == newItem.itemName && oldItem.itemSpecies == newItem.itemSpecies
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        val type = Type.values().first { it.name == item.itemType }
        holder.itemView.setOnClickListener {
            navController.navigate(
                ItemListFragmentDirections.actionItemListFragmentToItemViewFragment(
                    item.id,
                    type,
                    item.itemUri
                )
            )
        }
        holder.bind(item)

    }

}