package ru.mirea.ikbo1218.grachev.lab5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.ikbo1218.grachev.lab5.data.NetworkService
import ru.mirea.ikbo1218.grachev.lab5.data.Photo
import ru.mirea.ikbo1218.grachev.lab5.data.Vote

class LastTenVotedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_ten_voted)

        val rv = findViewById<RecyclerView>(R.id.rve_top_ten)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = LikedAdapter(this)
        (rv.adapter as LikedAdapter).notifyDataSetChanged()
    }

    class LikedAdapter(private val context: AppCompatActivity)
        : RecyclerView.Adapter<LikedAdapter.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)

        private var list: List<Vote> = NetworkService.lastTenLikes().filterNotNull().toList()
        private var pics: List<Photo> = listOf()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedAdapter.ViewHolder {
            val view = inflater.inflate(R.layout.recycler_view_element, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: LikedAdapter.ViewHolder, position: Int) {
            Thread {
                val ph = if (position < pics.size){
                    pics[position]
                }else{
                    val dto =
                        NetworkService.retrofit.getImgById(list[position].image_id).execute().body()!!
                    pics = pics + Photo(dto)
                    pics[position]
                }
                ph.attachTo(holder.img)
                context.runOnUiThread {
                    holder.like.setImageDrawable(context.getDrawable(R.drawable.like_placed))
                }
                holder.like.setOnClickListener {
                    NetworkService.delVote(ph.id)
                    ph.liked = -1
                    list = NetworkService.lastTenLikes().filterNotNull().toList()
                    pics = pics - pics[position]
                    context.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
                holder.dis.setOnClickListener {
                    NetworkService.vote(ph.id, 0)
                    ph.liked = 0
                    list = NetworkService.lastTenLikes().filterNotNull().toList()
                    pics = pics - pics[position]
                    context.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
            }.start()

        }

        class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            val img: ImageView = view.findViewById(R.id.img)
            val like: ImageButton = view.findViewById(R.id.btn_like)
            val dis: ImageButton = view.findViewById(R.id.btn_dislike)
        }
    }
}
