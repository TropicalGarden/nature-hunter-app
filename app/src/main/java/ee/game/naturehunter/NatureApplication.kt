package ee.game.naturehunter

import android.app.Application
import ee.game.naturehunter.data.ItemRoomDatabase

class NatureApplication : Application() {
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}