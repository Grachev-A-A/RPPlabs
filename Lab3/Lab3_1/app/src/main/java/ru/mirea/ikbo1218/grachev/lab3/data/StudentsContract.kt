package ru.mirea.ikbo1218.grachev.lab3.data

import android.provider.BaseColumns



class StudentsContract {
    class StudentEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "student"

            const val ID = BaseColumns._ID
            const val COLUMN_NAME = "fio"
            const val COLUMN_ADDED = "added"
        }
    }
}