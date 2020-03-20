package ru.mirea.ikbo1218.grachev.lab2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager


class VPActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vp)

        val vp = findViewById<ViewPager>(R.id.vp)
        vp.adapter = DataAdapter(this)
        vp.currentItem = intent.extras?.get("position") as Int
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("num", findViewById<ViewPager>(R.id.vp).currentItem)
        setResult(RESULT_OK, intent)
        finish()
    }

    class DataAdapter(private val context: AppCompatActivity) : PagerAdapter() {

        private val inflater = LayoutInflater.from(context)

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun getCount(): Int = DataReader.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = inflater.inflate(
                R.layout.pager, container,
                false
            )
            val iv = itemView.findViewById<ImageView>(R.id.pic)
            DataReader.setPic(iv, position, context)
            val n = itemView.findViewById<TextView>(R.id.name)
            n.text = DataReader.getName(position)
            val h = itemView.findViewById<TextView>(R.id.helptext)
            h.text = DataReader.getHelpText(position)

            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {

        }
    }
}
