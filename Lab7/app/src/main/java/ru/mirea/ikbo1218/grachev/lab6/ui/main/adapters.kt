package ru.mirea.ikbo1218.grachev.lab6.ui.main

import android.app.ActivityManager
import android.content.Context
import android.database.Cursor
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.ikbo1218.grachev.lab6.R
import ru.mirea.ikbo1218.grachev.lab6.data.DataListener
import ru.mirea.ikbo1218.grachev.lab6.data.DataProcessor
import ru.mirea.ikbo1218.grachev.lab6.data.GoodContract


class BackAdapter(context: AppCompatActivity): StoreAdapter<BackAdapter.VH>(context, true){
    class VH(view: View): RecyclerView.ViewHolder(view){
        val id: TextView = view.findViewById(R.id.id_tv)
        val name: EditText = view.findViewById(R.id.et_name)
        val cost: EditText = view.findViewById(R.id.et_cost)
        val amount: EditText = view.findViewById(R.id.et_amount)
        val btn: Button = view.findViewById(R.id.button2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            inflater.inflate(
                R.layout.back_item, parent, false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        if(position != dataCursor.count){
            dataCursor.moveToPosition(position)
            holder.amount.setText(dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_AMOUNT)).toString())
            holder.cost.setText(dataCursor.getDouble(dataCursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_COST)).toString())
            holder.id.text =
                dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.ID)).toString()
            holder.name.setText(dataCursor.getString(dataCursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_NAME)))
        }
        else{
            holder.amount.setText("")
            holder.name.setText("")
            holder.cost.setText("")
            holder.id.text = "*"
        }
        holder.btn.setOnClickListener {
            if(holder.name.text.toString().isBlank()){
                dp.remove(holder.id.text.toString().toInt()){
                    context.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Item with id ${holder.id.text.toString().toInt()} removed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else{
                dp.updateGood(
                    holder.id.text.toString(),
                    holder.name.text.toString(),
                    if(holder.amount.text.toString().isBlank()) 0 else holder.amount.text.toString().toInt(),
                    if(holder.cost.text.toString().isBlank()) 1.0 else holder.cost.text.toString().toDouble()
                ){
                    context.runOnUiThread {
                        Toast.makeText(
                            context,
                            "Item with id ${holder.id.text.toString().toInt()} updated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int = super.getItemCount() + 1
}


class FrontAdapter(context: AppCompatActivity): StoreAdapter<FrontAdapter.VH>(context, false){
    class VH(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.tv_name)
        val cost: TextView = view.findViewById(R.id.tv_cost)
        val amount: EditText = view.findViewById(R.id.et_amount)
        val btn: Button = view.findViewById(R.id.button)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            inflater.inflate(
                R.layout.front_item, parent, false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (!dataCursor.moveToPosition(position)) {
            dataCursor.close()
            dataCursor = dp.getDataCursor()
            if(!dataCursor.moveToPosition(position)) return
        }
        holder.name.text =
            dataCursor.getString(dataCursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_NAME))
        holder.cost.text = context.getString(
            R.string.cost,
            dataCursor.getDouble(dataCursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_COST))
        )
        holder.amount.apply {
            filters = arrayOf(MyInputFilter(dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_AMOUNT))))
            addTextChangedListener(MyTextWatcher(
                dataCursor.getDouble(dataCursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_COST)),
                holder, context
            ))
            setText("0")
        }

        holder.btn.setOnClickListener {
            val amount = holder.amount.text.toString().toInt()
            if(amount == 0){
                Toast.makeText(context, "Вы не указали количество товара!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dataCursor.moveToPosition(position)
            dp.sellGoods(
                dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.ID)),
                amount
            ){
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        if (it) "Successfully sold "
                        else "Can not sell "
                                + "$amount of items with id ${dataCursor.getInt(
                            dataCursor.getColumnIndex(
                                GoodContract.GoodEntry.ID
                            )
                        )}!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }
    class MyTextWatcher(private val cost: Double, private val holder: VH, private val context: Context): TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            holder.btn.text = context.getString(
                R.string.cost_all,
                (if(s.toString().isBlank()) 0 else s.toString().toInt()) * cost
            )
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    class MyInputFilter(
        private val amount: Int
    ): InputFilter{
        override fun filter(
            source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int
        ): CharSequence? {
            if(source == "") return null
            try{
                val str = StringBuilder()
                for(i in 0 until dstart) str.append(dest?.get(i))
                str.append(source)
                for(i in dend until (dest?.length?:0)) str.append(dest?.get(i))
                val input =  str.toString().toInt()
                if(input in 0..amount)
                    return null
                return ""
            }catch (ex: NumberFormatException){}
            return ""
        }
    }
}

abstract class StoreAdapter <VH: RecyclerView.ViewHolder>(internal val context: AppCompatActivity,
                                                          private val back: Boolean)
    : RecyclerView.Adapter<VH>(), DataListener{

    internal val inflater = LayoutInflater.from(context)
    internal val dp: DataProcessor = DataProcessor.getInstance(context)
    internal var dataCursor: Cursor = dp.getDataCursor(back)

    override fun getItemCount(): Int = dataCursor.count

    override fun onGoodAdded(id: Int) {
        if(!dataCursor.moveToFirst()
            || dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.ID)) > id
        ){
            dataCursor.close()
            dataCursor = dp.getDataCursor(back)
            context.runOnUiThread {
                notifyItemInserted(0)
            }
            return
        }
        for(i in 1 until dataCursor.count){
            if(
                dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.ID)) < id &&
                dataCursor.moveToNext() &&
                dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.ID)) > id
            ){
                dataCursor.close()
                dataCursor = dp.getDataCursor(back)
                context.runOnUiThread {
                    notifyItemInserted(i)
                }
                return
            }
        }
        dataCursor.close()
        dataCursor = dp.getDataCursor(back)
        context.runOnUiThread {
            notifyItemInserted(dataCursor.count - 1)
        }
    }

    override fun onGoodRemoved(id: Int) {
        dataCursor.moveToFirst()
        for(i in 0 until dataCursor.count){
            if(
                dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.ID)) == id
            ){
                dataCursor.close()
                dataCursor = dp.getDataCursor(back)
                context.runOnUiThread {
                    notifyItemRemoved(i)
                }
                return
            }
            dataCursor.moveToNext()
        }
        dataCursor.close()
        dataCursor = dp.getDataCursor(back)
        context.runOnUiThread {
            notifyDataSetChanged()
        }
    }

    override fun onGoodChanged(id: Int) {
        dataCursor.moveToFirst()
        for(i in 0 until dataCursor.count){
            if(
                dataCursor.getInt(dataCursor.getColumnIndex(GoodContract.GoodEntry.ID)) == id
            ){
                dataCursor.close()
                dataCursor = dp.getDataCursor(back)
                context.runOnUiThread {
                    notifyItemChanged(i)
                }
                return
            }
            dataCursor.moveToNext()
        }
    }
}

