package ru.mirea.ikbo1218.grachev.lab2

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import java.net.URL
import com.google.gson.GsonBuilder
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException


object DataReader {
    private val uri = "https://raw.githubusercontent.com/wesleywerner/ancient-tech/02decf875616dd9692b31658d92e64a20d99f816/src/data/techs.ruleset.json"
    private val picUri = "https://raw.githubusercontent.com/wesleywerner/ancient-tech/02decf875616dd9692b31658d92e64a20d99f816/src/images/tech/"
    private val json:String
    private val data: ArrayList<DataUnit>
    private var img:ArrayList<Bitmap?> = ArrayList()
    private var notLoaded:HashMap<Int, Pair<ImageView, AppCompatActivity>> = HashMap()
    val size:Int
        get() = data.size

    init {
        val tmp = URL(uri).readText()
        json = "[" + tmp.substringAfter("},")

        val builder = GsonBuilder()
        val gson = builder.create()
        val groupListType = object : TypeToken<ArrayList<DataUnit>>() {}.type

        data = gson.fromJson(json,groupListType)

        Thread{
            this.loadPics()
            for(i in notLoaded.keys)
                setPic(notLoaded[i]!!.first, i, notLoaded[i]!!.second)
        }.start()

     }

    fun getHelpText(num:Int):String{
        if(data[num].helptext != null)
            return data[num].helptext!!
        return "Нет данных"
    }

    fun getName(num:Int): String = data[num].name


    fun setPic(im:ImageView, num:Int, a: AppCompatActivity){
        a.runOnUiThread{
            if(img.size <= num){
                im.setImageDrawable(ContextCompat.getDrawable(im.context, R.drawable.loadind_ic))
                notLoaded[num] = Pair(im, a)
            }
            else if (img[num] == null)
                im.setImageDrawable(ContextCompat.getDrawable(im.context, R.drawable.file_not_found_ic))
            else
                im.setImageBitmap(img[num])
        }


    }


    private fun loadPics(){
        for (i in data.indices){
            val d = data[i]
            val bm:Bitmap
            try{
                bm = BitmapFactory.decodeStream(URL(picUri + d.graphic).openStream())
                img.add(bm)
            }catch (e: FileNotFoundException){
                img.add(null)
            }
            if(notLoaded.containsKey(i)){
                setPic(notLoaded[i]!!.first, i, notLoaded[i]!!.second)
                notLoaded.remove(i)
            }

        }
    }

    class DataUnit(val flags:String, val graphic:String,
                        val graphic_alt:String, val name:String,
                        val req1:String, val req2:String,
                        val helptext:String?)
}