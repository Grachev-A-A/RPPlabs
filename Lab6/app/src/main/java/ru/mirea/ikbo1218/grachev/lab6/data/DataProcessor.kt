package ru.mirea.ikbo1218.grachev.lab6.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import ru.mirea.ikbo1218.grachev.lab6.ui.main.FrontAdapter

class DataProcessor private constructor(context: Context) {
    companion object {
        private var instance: DataProcessor? = null

        @JvmStatic
        fun getInstance(context: Context): DataProcessor {
            if (instance == null)
                instance = DataProcessor(context)
            return instance!!
        }
    }
    private val listeners: MutableList<DataListener> = mutableListOf()
    private val mDB = GoodOpenHelper(context)

    fun startListening(listener: DataListener): Boolean{
        if(listeners.contains(listener)) return false
        listeners.add(listener)
        return true
    }
    fun stopListening(listener: DataListener): Boolean{
        if(!listeners.contains(listener)) return false
        listeners.remove(listener)
        return true
    }

    fun getDataCursor(back: Boolean = false):Cursor = if (!back)
        mDB.readableDatabase.rawQuery(
            "SELECT * FROM ${GoodContract.GoodEntry.TABLE_NAME} WHERE ${GoodContract.GoodEntry.COLUMN_AMOUNT} > 0",
            null
        )
    else mDB.readableDatabase.rawQuery(
        "SELECT * FROM ${GoodContract.GoodEntry.TABLE_NAME}",
        null
    )

    fun sellGoods(id: Int, amount: Int): Boolean{
        val amountOld = mDB.readableDatabase.rawQuery(
            "SELECT ${GoodContract.GoodEntry.COLUMN_AMOUNT} FROM ${GoodContract.GoodEntry.TABLE_NAME} WHERE ${GoodContract.GoodEntry.ID} = $id"
                , null
        ).use {
            it.moveToFirst()
            val res = it.getInt(it.getColumnIndex(GoodContract.GoodEntry.COLUMN_AMOUNT))
            it.close()
            res
        }
        if(amountOld - amount < 0) return false
        mDB.writableDatabase.execSQL(
            "UPDATE ${GoodContract.GoodEntry.TABLE_NAME} " +
                    "SET ${GoodContract.GoodEntry.COLUMN_AMOUNT} = ${amountOld - amount} " +
                    "WHERE ${GoodContract.GoodEntry.ID} = $id"
        )
        listeners.forEach {
            it.onGoodChanged(id)
            if(amountOld - amount == 0 && it is FrontAdapter)
                it.onGoodRemoved(id)
        }
        return true
    }

    fun updateGood(id: String, name: String, amount: Int, cost: Double){
        val writable = mDB.writableDatabase
        val vs = ContentValues()
        vs.put(GoodContract.GoodEntry.COLUMN_NAME, name)
        vs.put(GoodContract.GoodEntry.COLUMN_COST, cost)
        vs.put(GoodContract.GoodEntry.COLUMN_AMOUNT, amount)
        if(id == "*"){
            val id = writable.insert(
                GoodContract.GoodEntry.TABLE_NAME, null, vs
            )
            listeners.forEach {
                it.onGoodAdded(id.toInt())
            }
        }else{
            val id: Int = id.toInt()
            val cursor = mDB.readableDatabase.rawQuery(
                "SELECT ${GoodContract.GoodEntry.COLUMN_AMOUNT} FROM ${GoodContract.GoodEntry.TABLE_NAME} WHERE ${GoodContract.GoodEntry.ID} = $id"
                , null
            )
            cursor.moveToFirst()
            val oldAmount = cursor.getInt(cursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_AMOUNT))
            writable.update(
                GoodContract.GoodEntry.TABLE_NAME, vs, "${GoodContract.GoodEntry.ID}=?", arrayOf(id.toString())
            )
            listeners.forEach {
                it.onGoodChanged(id)
                if(amount == 0 && it is FrontAdapter)
                    it.onGoodRemoved(id)
                if(oldAmount == 0 && it is FrontAdapter){
                    it.onGoodAdded(id)
                }
            }
        }
    }

    fun remove(id:Int){
        mDB.writableDatabase.delete(
            GoodContract.GoodEntry.TABLE_NAME, "${GoodContract.GoodEntry.ID}=?", arrayOf(id.toString())
        )
        listeners.forEach {
            it.onGoodRemoved(id)
        }
    }

}