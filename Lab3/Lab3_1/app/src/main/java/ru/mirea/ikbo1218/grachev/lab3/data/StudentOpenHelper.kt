package ru.mirea.ikbo1218.grachev.lab3.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.Serializable

class StudentOpenHelper (private val context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, db_version),
    Serializable{

    override fun onCreate(db: SQLiteDatabase?) {
        val createStr = "CREATE TABLE " + StudentsContract.StudentEntry.TABLE_NAME + " (" +
                StudentsContract.StudentEntry.ID + " INTEGER PRIMARY KEY, " +
                StudentsContract.StudentEntry.COLUMN_NAME + " VARCHAR(255) NOT NULL, " +
                StudentsContract.StudentEntry.COLUMN_ADDED + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)"

        db?.execSQL(createStr)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE "+ StudentsContract.StudentEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "students.db"

        var db_version = 1
    }


}
