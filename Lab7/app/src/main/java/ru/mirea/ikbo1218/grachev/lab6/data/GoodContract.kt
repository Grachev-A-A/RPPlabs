package ru.mirea.ikbo1218.grachev.lab6.data

import android.provider.BaseColumns

class GoodContract {
    class GoodEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "good"

            const val ID = BaseColumns._ID
            const val COLUMN_NAME = "name"
            const val COLUMN_COST = "cost"
            const val COLUMN_AMOUNT = "amount"
        }
    }
}