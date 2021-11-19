package ee.game.naturehunter.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import ee.game.naturehunter.constant.Type
import ee.game.naturehunter.data.ItemDao
import ee.game.naturehunter.data.model.Item
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI

class ItemViewModel(private val itemDao: ItemDao, private val application: Application) : ViewModel() {

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
        val appContext = application.applicationContext
        val fileName = uri.path!!.substringAfterLast("/")
        val filePath = "${appContext.filesDir.path}/$fileName"
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
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

class ItemViewModelFactory(private val itemDao: ItemDao, private val application: Application) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
