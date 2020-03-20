package ru.mirea.ikbo1218.grachev.lab3

import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ru.mirea.ikbo1218.grachev.lab3.data.StudentOpenHelper
import ru.mirea.ikbo1218.grachev.lab3.data.StudentsContract

import kotlin.random.Random
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private val mData = StudentOpenHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.show_button).setOnClickListener {
            val i = Intent(this, ListActivity::class.java)
            startActivity(i)
        }

        findViewById<Button>(R.id.add_button).setOnClickListener{
            val cursor = mData.readableDatabase.rawQuery("SELECT " +
                    StudentsContract.StudentEntry.COLUMN_LAST_NAME + ", "
                    + StudentsContract.StudentEntry.COLUMN_FIRST_NAME + ", "
                    + StudentsContract.StudentEntry.COLUMN_MIDDLE_NAME + " FROM " +
                    StudentsContract.StudentEntry.TABLE_NAME, null)
            cursor.moveToFirst()
            var added = false
            for (i in 1..29){
                val st = getString(resources.getIdentifier("gm$i", "string", packageName))
                var found = false
                for(j in 0 until cursor.count) {
                    if (st == cursor.getString(cursor.getColumnIndex(StudentsContract.StudentEntry.COLUMN_LAST_NAME)) + " " +
                            cursor.getString(cursor.getColumnIndex(StudentsContract.StudentEntry.COLUMN_FIRST_NAME)) + " " +
                            cursor.getString(cursor.getColumnIndex(StudentsContract.StudentEntry.COLUMN_MIDDLE_NAME))){
                        found = true
                        break
                    }
                    cursor.moveToNext()
                }
                cursor.moveToFirst()

                if(!found){
                    val divided = st.split(" ")
                    val vs = ContentValues()
                    vs.put(StudentsContract.StudentEntry.COLUMN_LAST_NAME, divided[0])
                    vs.put(StudentsContract.StudentEntry.COLUMN_FIRST_NAME, divided[1])
                    vs.put(StudentsContract.StudentEntry.COLUMN_MIDDLE_NAME, divided[2])

                    mData.writableDatabase.insert(StudentsContract.StudentEntry.TABLE_NAME,
                        null, vs)

                    Toast.makeText(
                        applicationContext,
                        "Добавлена запись: $st", Toast.LENGTH_SHORT
                    ).show()
                    added = true
                    break
                }

            }
            cursor.close()
            if(!added)
                Toast.makeText(
                    applicationContext,
                    "Одногруппников больше нет!", Toast.LENGTH_SHORT
                ).show()
        }

        findViewById<Button>(R.id.update_button).setOnClickListener{
            val db = mData.writableDatabase

            val cursor = db.rawQuery("SELECT MAX(" + StudentsContract.StudentEntry.ID +
                    ") AS mID FROM " + StudentsContract.StudentEntry.TABLE_NAME, null)
            cursor.moveToFirst()
            val id = cursor.getInt(cursor.getColumnIndex("mID"))

            cursor.close()

            val vs = ContentValues()
            vs.put(StudentsContract.StudentEntry.COLUMN_LAST_NAME, "Иванов")
            vs.put(StudentsContract.StudentEntry.COLUMN_FIRST_NAME, "Иван")
            vs.put(StudentsContract.StudentEntry.COLUMN_MIDDLE_NAME, "Иванович")
            db.update(StudentsContract.StudentEntry.TABLE_NAME,
                vs,
                StudentsContract.StudentEntry.ID + " = ?",
                arrayOf(id.toString())
                )
        }



//        if(savedInstanceState == null ||! savedInstanceState.getBoolean("started")) {
//            val db = mData.writableDatabase
//
//            db.delete(StudentsContract.StudentEntry.TABLE_NAME, null, null)
//            val r = Random(Calendar.getInstance().timeInMillis)
//            for (i in 0..4) {
//                val cv = ContentValues()
//                cv.put(
//                    StudentsContract.StudentEntry.COLUMN_NAME,
//                    getString(
//                        resources.getIdentifier(
//                            "gm${r.nextInt(27) + 1}",
//                            "string", packageName
//                        )
//                    )
//                )
//                db.insert(StudentsContract.StudentEntry.TABLE_NAME, null, cv)
//            }
//        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("started", true)
    }
}
