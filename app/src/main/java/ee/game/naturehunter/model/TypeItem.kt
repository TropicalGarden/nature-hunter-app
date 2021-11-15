package ee.game.naturehunter.model

import ee.game.naturehunter.constant.C
import ee.game.naturehunter.constant.Type
import ee.game.naturehunter.data.model.Item

data class TypeItem(
    val type: Type,
    val items: List<Item>,
    var rank: Int = C.STARTING_RANK,
    var multiplier: Int = C.STARTING_MULTIPLIER,
    var levelUpCriteria: Int = C.STARTING_RANK_UP_CRITERIA,
    var points: Int = 0,
    var multiplierProgress: Int = 0
)