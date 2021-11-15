package com.example.naturehunter.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.example.naturehunter.constant.Type
import com.example.naturehunter.data.ItemDao
import com.example.naturehunter.data.model.Item
import kotlinx.coroutines.launch
import java.io.File

class ItemViewModel(private val itemDao: ItemDao) : ViewModel() {

    lateinit var items: LiveData<List<Item>>

    lateinit var pictureUri: String

    fun updateItems(type: Type) {
        items = itemDao.getItemsByType(type.name).asLiveData()
    }

    private fun insertItem(item: Item) {
        viewModelScope.launch { itemDao.insert(item) }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            itemDao.delete(id)
        }
    }

    fun deletePictureFile(uri: Uri) {
        File(uri.path!!).delete()
    }

    private fun updateItem(item: Item) {
        viewModelScope.launch { itemDao.update(item) }
    }

    @Suppress("NAME_SHADOWING")
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemSpecies: String?,
        itemType: Type,
        itemUri: String
    ) {
        val itemSpecies = if (itemSpecies.isNullOrEmpty()) "-" else itemSpecies
        updateItem(Item(itemId, itemName, itemSpecies, itemType.name, itemUri))
    }

    @Suppress("NAME_SHADOWING")
    fun addItem(
        itemName: String,
        itemSpecies: String?,
        itemType: Type,
        itemUri: String
    ) {
        val itemSpecies = if (itemSpecies.isNullOrEmpty()) "-" else itemSpecies
        insertItem(
            Item(
                itemName = itemName,
                itemSpecies = itemSpecies,
                itemType = itemType.name,
                itemUri = itemUri
            )
        )
    }

    fun isEntryValid(itemName: String): Boolean {
        return itemName.isNotBlank()
    }

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

}

class ItemViewModelFactory(private val itemDao: ItemDao) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
