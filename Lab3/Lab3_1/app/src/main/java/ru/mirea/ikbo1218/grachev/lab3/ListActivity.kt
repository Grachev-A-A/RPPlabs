package ru.mirea.ikbo1218.grachev.lab3

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.ikbo1218.grachev.lab3.data.StudentOpenHelper
import ru.mirea.ikbo1218.grachev.lab3.data.StudentsContract

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        val rw = findViewById<RecyclerView>(R.id.resView)
        rw.adapter = DataAdapter(this, StudentOpenHelper(this))
    }

    class DataAdapter(private val context: AppCompatActivity,
                      private val mDb: StudentOpenHelper) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapter.ViewHolder {
            val view = inflater.inflate(R.layout.rv_item, parent, false)
            return ViewHolder(view)
        }


        override fun getItemCount(): Int {
            val cursor = mDb.readableDatabase
                .rawQuery("SELECT COUNT(*) AS cnt FROM " + StudentsContract.StudentEntry.TABLE_NAME, arrayOf())

            cursor.moveToFirst()
            val res = cursor.getInt(cursor.getColumnIndex("cnt"))
            cursor.close()
            return res

        }
        override fun onBindViewHolder(holder: DataAdapter.ViewHolder, position: Int) {
            val cursor = mDb.readableDatabase
                .rawQuery("SELECT * FROM "+ StudentsContract.StudentEntry.TABLE_NAME +
                        " LIMIT 1 OFFSET " + position
                    , arrayOf())

            cursor.moveToFirst()
            holder.num.text = context.getString(R.string.id,
                cursor.getInt(cursor.getColumnIndex(StudentsContract.StudentEntry.ID)))

            holder.date.text = cursor.getString(cursor.getColumnIndex(StudentsContract.StudentEntry.COLUMN_ADDED))
            holder.name.text = cursor.getString(cursor.getColumnIndex(StudentsContract.StudentEntry.COLUMN_NAME))

            cursor.close()

            val color = if (position % 2 == 1)
                ContextCompat.getColor(inflater.context, R.color.colorEven)
            else
                ContextCompat.getColor(inflater.context, R.color.colorOdd)
            holder.rvi.setBackgroundColor(color)
        }


        class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            val name: TextView = view.findViewById(R.id.fio)
            val date: TextView = view.findViewById(R.id.date)
            val num: TextView = view.findViewById(R.id.num)
            val rvi: LinearLayout = view.findViewById(R.id.rvi)
        }
    }
}
