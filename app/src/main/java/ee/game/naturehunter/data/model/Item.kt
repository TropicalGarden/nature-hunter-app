package ee.game.naturehunter.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "item_name")
    val itemName: String,
    @ColumnInfo(name = "item_species")
    val itemSpecies: String,
    @ColumnInfo(name = "item_type")
    val itemType: String,
    @ColumnInfo(name = "item_uri")
    val itemUri: String
)