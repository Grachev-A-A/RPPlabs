package ru.mirea.ikbo1218.grachev.lab6.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


internal class GoodOpenHelper(
    context: Context
): SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {
    companion object{
        private const val DB_NAME = "store"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createStr = "CREATE TABLE ${GoodContract.GoodEntry.TABLE_NAME} (" +
                "${GoodContract.GoodEntry.ID} INTEGER PRIMARY KEY, " +
                "${GoodContract.GoodEntry.COLUMN_NAME} VARCHAR(255) NOT NULL, " +
                "${GoodContract.GoodEntry.COLUMN_COST} FLOAT NOT NULL DEFAULT 0, " +
                "${GoodContract.GoodEntry.COLUMN_AMOUNT} INTEGER NOT NULL DEFAULT 0"  +
                ")"
        db?.execSQL(createStr)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}