package com.example.naturehunter

import android.app.Application
import com.example.naturehunter.data.ItemRoomDatabase

class NatureApplication : Application() {
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}