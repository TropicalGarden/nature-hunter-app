package com.example.naturehunter.viewmodel

import androidx.lifecycle.*
import com.example.naturehunter.constant.C
import com.example.naturehunter.constant.Type
import com.example.naturehunter.data.ItemDao
import com.example.naturehunter.data.model.Item
import com.example.naturehunter.model.TypeItem

class CategoryViewModel(itemDao: ItemDao) : ViewModel() {

    val items: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    private var _typeItems = mutableListOf<TypeItem>()
    val typeItems get() = _typeItems

    private var _totalPoints = MutableLiveData(0)
    val totalPoints: LiveData<Int> get() = _totalPoints

    private val _totalHunts = MutableLiveData(0)
    val totalHunts: LiveData<Int> get() = _totalHunts

    private val _rank = MutableLiveData(C.STARTING_RANK)
    val rank: LiveData<Int> get() = _rank

    private var _rankProgress = MutableLiveData(0)
    val rankProgress: LiveData<Int> get() = _rankProgress

    private var _rankPoints = MutableLiveData(0)
    val rankPoints: LiveData<Int> get() = _rankPoints

    private var _rankUpCriteria = MutableLiveData(C.STARTING_RANK_UP_CRITERIA)
    val rankUpCriteria: LiveData<Int> get() = _rankUpCriteria

    fun update() {
        createTypeItems()
        updateAllTypeValues()
        updateTotalPoints()
        updateTotalHunts()
        updateRankValues()
    }

    private fun createTypeItems() {
        val typeItems = mutableListOf<TypeItem>()
        val itemsWithSameType = mutableListOf<Item>()
        for (type in Type.values()) {
            items.value?.let { item ->
                item.forEach { if (it.itemType == type.name) itemsWithSameType.add(it) }
            }
            typeItems.add(TypeItem(type, ArrayList(itemsWithSameType)))
            itemsWithSameType.clear()
        }
        _typeItems = typeItems
    }

    private fun updateAllTypeValues() {
        Type.values().forEach { updateTypeValues(it) }
    }

    private fun updateTypeValues(type: Type) {
        val typeItem = typeItems.first { it.type == type }
        var multiplier = C.STARTING_MULTIPLIER
        var multiplierCriteriaIncrement = C.MULTIPLIER_CRITERIA_INCREMENT
        var multiplierCriteria = C.STARTING_MULTIPLIER_CRITERIA
        var points = 0
        var itemCount = 0

        for (item in typeItem.items) {
            itemCount++
            if (itemCount == multiplierCriteria && multiplier < C.MAXIMUM_MULTIPLIER) {
                points += type.reward * multiplier
                multiplier++
                if (multiplier % 2 != 0) {
                    multiplierCriteriaIncrement++
                }
                multiplierCriteria += multiplierCriteriaIncrement
            } else {
                points += type.reward * multiplier
            }
        }
        typeItem.points = points
        typeItem.multiplier = multiplier
        updateMultiplierProgress(typeItem, multiplierCriteria, multiplierCriteriaIncrement)
    }

    private fun updateMultiplierProgress(
        typeItem: TypeItem,
        criteria: Int,
        multiplierCriteriaIncrement: Int
    ) {
        var progressCount = typeItem.items.size.toFloat()
        var progressCriteria = criteria.toFloat()

        if (progressCount >= C.STARTING_MULTIPLIER_CRITERIA) {
            val progressSubtract = criteria - multiplierCriteriaIncrement
            progressCount -= progressSubtract
            progressCriteria -= progressSubtract
        }
        typeItem.multiplierProgress =
            (progressCount / progressCriteria * C.PERCENTAGE_MULTIPLIER).toInt()
    }

    private fun updateTotalPoints() {
        var totalPoints = 0
        typeItems.forEach { totalPoints += it.points }
        _totalPoints.value = totalPoints
    }

    private fun updateTotalHunts() {
        var totalHunts = 0
        typeItems.forEach { totalHunts += it.items.size }
        _totalHunts.value = totalHunts
    }

    private fun updateRankValues() {
        var rankPointsSubtract = 0
        _rank.value = C.STARTING_RANK
        _rankUpCriteria.value = C.STARTING_RANK_UP_CRITERIA
        updateRankPoints(rankPointsSubtract)
        while (rankPoints.value!! >= rankUpCriteria.value!! && rank.value!! < C.FINAL_RANK) {
            _rank.value = rank.value!! + 1
            if (rank.value!! < C.FINAL_RANK) {
                rankPointsSubtract += rankUpCriteria.value!!
                _rankUpCriteria.value = rankUpCriteria.value!! * C.RANK_UP_CRITERIA_MULTIPLIER
            }
            updateRankPoints(rankPointsSubtract)
        }
        updateRankProgress()
    }

    private fun updateRankPoints(rankPointsSubtract: Int) {
        _rankPoints.value = totalPoints.value!! - rankPointsSubtract
    }

    private fun updateRankProgress() {
        _rankProgress.value =
            (rankPoints.value!!.toFloat() / rankUpCriteria.value!!.toFloat() * C.PERCENTAGE_MULTIPLIER).toInt()
    }

}

class CategoryViewModelFactory(private val itemDao: ItemDao) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
