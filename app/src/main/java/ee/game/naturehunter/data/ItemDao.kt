package ee.game.naturehunter.data

import androidx.room.*
import ee.game.naturehunter.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getItems(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE item_type = :type ORDER BY id DESC")
    fun getItemsByType(type: String): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Query("DELETE FROM item WHERE id = :id")
    suspend fun delete(id: Int)
}