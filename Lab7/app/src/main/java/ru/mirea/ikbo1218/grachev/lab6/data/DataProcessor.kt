package ru.mirea.ikbo1218.grachev.lab6.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import ru.mirea.ikbo1218.grachev.lab6.ui.main.FrontAdapter
import java.util.*

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
    private var isChanging: Boolean = false
    private val threads: Queue<Thread> = LinkedList()
    private val r:Random = Random()

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

    private fun change(command: ()->Boolean, callback: (Boolean) -> Unit = {}){
        threads.add(Thread{
            Thread.sleep((r.nextInt()%2+3) * 1000L)
            callback(command())
            if(!threads.isEmpty())
                threads.remove().start()
            else isChanging = false
        })
        if(!isChanging){
            isChanging = true
            threads.remove().start()
        }
    }

    fun sellGoods(id: Int, amount: Int, callback: (Boolean) -> Unit = {}): Boolean{
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
        change({
            val amountOld = mDB.readableDatabase.rawQuery(
                "SELECT ${GoodContract.GoodEntry.COLUMN_AMOUNT} FROM ${GoodContract.GoodEntry.TABLE_NAME} WHERE ${GoodContract.GoodEntry.ID} = $id"
                , null
            ).use {
                it.moveToFirst()
                val res = it.getInt(it.getColumnIndex(GoodContract.GoodEntry.COLUMN_AMOUNT))
                it.close()
                res
            }

            if(amountOld - amount < 0) return@change false
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
            return@change true
        }, callback)

        return true
    }

    fun updateGood(id: String, name: String, amount: Int, cost: Double, callback: (Boolean) -> Unit = {}){
        val vs = ContentValues()
        vs.put(GoodContract.GoodEntry.COLUMN_NAME, name)
        vs.put(GoodContract.GoodEntry.COLUMN_COST, cost)
        vs.put(GoodContract.GoodEntry.COLUMN_AMOUNT, amount)
        if(id == "*"){
            change({
                with(mDB.writableDatabase){
                    val id = this.insert(
                        GoodContract.GoodEntry.TABLE_NAME, null, vs
                    )
                    listeners.forEach {
                        it.onGoodAdded(id.toInt())
                    }
                }
                return@change true
            }, callback)

        }else{
            change({
                val id: Int = id.toInt()
                val cursor = mDB.readableDatabase.rawQuery(
                    "SELECT ${GoodContract.GoodEntry.COLUMN_AMOUNT} FROM ${GoodContract.GoodEntry.TABLE_NAME} WHERE ${GoodContract.GoodEntry.ID} = $id"
                    , null
                )
                cursor.moveToFirst()
                val oldAmount = cursor.getInt(cursor.getColumnIndex(GoodContract.GoodEntry.COLUMN_AMOUNT))
                cursor.close()
                mDB.writableDatabase.update(
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
                true
            }, callback)

        }
    }

    fun remove(id:Int, callback: (Boolean) -> Unit = {}) {
        change({
            mDB.writableDatabase.delete(
                GoodContract.GoodEntry.TABLE_NAME,
                "${GoodContract.GoodEntry.ID}=?",
                arrayOf(id.toString())
            )
            listeners.forEach {
                it.onGoodRemoved(id)
            }
            true
        }, callback)
    }

}