package com.iscoding.qrcode.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Scanned::class, Generated::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scannedDao(): ScannedDao
    abstract fun generatedDao(): GeneratedDao
    companion object {
        const val DATABASE_NAME = "QrCode_db"
    }
}