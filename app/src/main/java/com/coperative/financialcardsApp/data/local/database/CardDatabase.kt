package com.coperative.financialcardsApp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.coperative.financialcardsApp.data.local.dao.CardDao
import com.coperative.financialcardsApp.data.local.entities.CardEntity
import com.coperative.financialcardsApp.data.local.entities.TransactionEntity
import com.coperative.financialcardsApp.data.local.RoomTypeConverters

@Database(entities = [CardEntity::class, TransactionEntity::class], version = 1)
@TypeConverters(RoomTypeConverters::class)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}
