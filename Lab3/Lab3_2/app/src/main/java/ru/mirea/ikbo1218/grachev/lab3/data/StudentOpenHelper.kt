package ru.mirea.ikbo1218.grachev.lab3.data

import android.content.ContentValues
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
                StudentsContract.StudentEntry.COLUMN_FIRST_NAME + " VARCHAR(32) NOT NULL, " +
                StudentsContract.StudentEntry.COLUMN_LAST_NAME + " VARCHAR(32) NOT NULL, " +
                StudentsContract.StudentEntry.COLUMN_MIDDLE_NAME + " VARCHAR(64) NOT NULL, " +
                StudentsContract.StudentEntry.COLUMN_ADDED + " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)"

        db?.execSQL(createStr)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var updVer = oldVersion
        when(updVer){
            1-> {
                updVer++
                val cursor = db?.rawQuery("SELECT * FROM " + StudentsContract.StudentEntry.TABLE_NAME, null)
                val vss: ArrayList<ContentValues> = arrayListOf()
                if(cursor?.moveToFirst()!!)
                    do{
                        val vs = ContentValues()
                        vs.put(StudentsContract.StudentEntry.ID, cursor.getInt(cursor.getColumnIndex(StudentsContract.StudentEntry.ID)))
                        val fio = cursor.getString(cursor.getColumnIndex(StudentsContract.StudentEntry.COLUMN_NAME))
                        val f_i_o = fio.split(" ")
                        vs.put(StudentsContract.StudentEntry.COLUMN_LAST_NAME, f_i_o[0])
                        vs.put(StudentsContract.StudentEntry.COLUMN_FIRST_NAME, f_i_o[1])
                        vs.put(StudentsContract.StudentEntry.COLUMN_MIDDLE_NAME, f_i_o[2])
                        vs.put(StudentsContract.StudentEntry.COLUMN_ADDED, cursor.getString(cursor.getColumnIndex(StudentsContract.StudentEntry.COLUMN_ADDED)))

                        vss.add(vs)
                    }
                    while(cursor.moveToNext())
                cursor.close()
                
                
                db.execSQL("DROP TABLE "+ StudentsContract.StudentEntry.TABLE_NAME)
                onCreate(db)
                for (v in vss){
                    db.insert(StudentsContract.StudentEntry.TABLE_NAME, null, v)
                }
                
            }
        }

    }



    companion object {
        const val DB_NAME = "students.db"

        var db_version = 2
    }


}
