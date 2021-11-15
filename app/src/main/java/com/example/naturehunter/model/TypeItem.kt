package com.example.naturehunter.model

import com.example.naturehunter.constant.C
import com.example.naturehunter.constant.Type
import com.example.naturehunter.data.model.Item

data class TypeItem(
    val type: Type,
    val items: List<Item>,
    var rank: Int = C.STARTING_RANK,
    var multiplier: Int = C.STARTING_MULTIPLIER,
    var levelUpCriteria: Int = C.STARTING_RANK_UP_CRITERIA,
    var points: Int = 0,
    var multiplierProgress: Int = 0
)