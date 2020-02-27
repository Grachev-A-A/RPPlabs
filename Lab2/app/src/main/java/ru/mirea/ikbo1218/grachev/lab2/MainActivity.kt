package ru.mirea.ikbo1218.grachev.lab2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rw = findViewById<RecyclerView>(R.id.resView)
        rw.adapter = DataAdapter(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                val r = data?.getIntExtra("num", -1)
                if (r != null && r>=0) {
                    findViewById<RecyclerView>(R.id.resView).scrollToPosition(r)
                }
            }
        }


    }

    class DataAdapter(private val context: AppCompatActivity) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataAdapter.ViewHolder {
            val view = inflater.inflate(R.layout.recycler_view_element, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = DataReader.size

        override fun onBindViewHolder(holder: DataAdapter.ViewHolder, position: Int) {
            val color = if (position % 2 == 1)
                ContextCompat.getColor(inflater.context, R.color.colorEven)
            else
                ContextCompat.getColor(inflater.context, R.color.colorOdd)
            holder.rve.setBackgroundColor(color)
            DataReader.setPic(holder.iv, position, context)
            holder.tv.text = DataReader.getName(position)

            holder.rve.setOnClickListener {
                val i = Intent(inflater.context, VPActivity::class.java)
                i.putExtra("position", position)
                context.startActivityForResult(i, 1)
            }
        }

        class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            val iv:ImageView = view.findViewById(R.id.pic)
            val tv: TextView = view.findViewById(R.id.text)
            val rve: LinearLayout = view.findViewById(R.id.rve)
        }
    }
}
