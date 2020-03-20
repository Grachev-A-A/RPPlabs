package ru.mirea.ikbo1218.grachev.lab3.data

import android.provider.BaseColumns



class StudentsContract {
    class StudentEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "student"

            const val ID = BaseColumns._ID
            @Deprecated("bd v 1")
            const val COLUMN_NAME = "fio"
            const val COLUMN_ADDED = "added"

            const val COLUMN_FIRST_NAME = "name"
            const val COLUMN_LAST_NAME = "last_name"
            const val COLUMN_MIDDLE_NAME = "middle_name"
        }
    }
}