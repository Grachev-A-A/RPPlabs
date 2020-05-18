package ru.mirea.ikbo1218.grachev.lab6.data

interface DataListener{
    fun onGoodAdded(id:Int)
    fun onGoodRemoved(id:Int)
    fun onGoodChanged(id: Int)
}