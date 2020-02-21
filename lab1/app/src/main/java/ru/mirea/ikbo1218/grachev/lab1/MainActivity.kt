package ru.mirea.ikbo1218.grachev.lab1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ibm.icu.text.RuleBasedNumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rw = findViewById<RecyclerView>(R.id.resView)
        rw.adapter = DataAdapter(this)
    }

}

class DataAdapter(context:Context) : RecyclerView.Adapter<DataAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.recycler_view_element, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = 1000000

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = if (position % 2 == 1)
            ContextCompat.getColor(inflater.context, R.color.colorEven)
        else
            ContextCompat.getColor(inflater.context, R.color.colorOdd)
        holder.rve.setBackgroundColor(color)
        holder.tv.text = nf.format(position+1)
//        val sNum = (position + 1).toString()
//        var sRes = ""
//        if (sNum == "1000000") sRes = "Миллион"
//        else{
//            var cont = false
//            for (index in sNum.indices){
//                val c = sNum[index]
//                if(cont){
//                    cont = false
//                    continue
//                }
//                val num = sNum.length - 1 - index
//                val i:Int
//                if ((num == 4 || num == 1) && c == '1'){
//                    cont = true
//                    i = inflater.context.resources.getIdentifier(
//                        "fir$c${sNum[index+1]}",
//                        "string",
//                        inflater.context.packageName
//                    )
//                    sRes += inflater.context.getString(i) + " "
//                    if(num==4) sRes += inflater.context.getString(R.string.for0) + " "
//                }
//                else{
//                    val shortcut = arrayOf("fir", "sec", "thi", "for", "fir", "sec")
//                    i = inflater.context.resources.getIdentifier(
//                        "${shortcut[num]}$c",
//                        "string",
//                        inflater.context.packageName
//                    )
//                    try{
//                        sRes += inflater.context.getString(i)+ " "
//                    }catch (e: Exception){
//                        println("num = $num, shortcut[num] = ${shortcut[num]}, c = $c")
//                    }
//                }
//            }
//
//        }
//        holder.tv.text = sRes
    }
    private val nf = RuleBasedNumberFormat(
        Locale("ru", "RU"),
        RuleBasedNumberFormat.SPELLOUT
    )

    private val inflater = LayoutInflater.from(context)

    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val tv:TextView = view.findViewById(R.id.text)
        val rve: LinearLayout = view.findViewById(R.id.rve)
    }
}