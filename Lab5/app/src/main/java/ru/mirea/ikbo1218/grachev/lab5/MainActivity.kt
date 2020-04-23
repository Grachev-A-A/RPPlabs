package ru.mirea.ikbo1218.grachev.lab5


import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ru.mirea.ikbo1218.grachev.lab5.data.NetworkService
import ru.mirea.ikbo1218.grachev.lab5.data.Photo
import ru.mirea.ikbo1218.grachev.lab5.data.PhotoDTO


class MainActivity : AppCompatActivity() {
    private lateinit var rv:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv = findViewById(R.id.resView)
        rv.layoutManager = LinearLayoutManager(this)

        val sp = findViewById<Spinner>(R.id.breeds_selector)
        sp.adapter = ArrayAdapter(
                this, R.layout.drop_down_row_layout,
                NetworkService.getBreeds()
        )

        findViewById<Button>(R.id.start_search_btn).setOnClickListener{
            rv.smoothScrollToPosition(0)
            rv.adapter = DataAdapter(
                this, null,
                NetworkService.getBreedId(sp.selectedItem.toString())
            )
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val groupListType = object : TypeToken<List<PhotoDTO?>>() {}.type
        val lst: List<PhotoDTO?> = GsonBuilder().create().fromJson(
            savedInstanceState.getString("Imgs", "[]"),
            groupListType
        )
        val l = mutableListOf<Photo>()
        for (it in lst.filterNotNull())
            l += Photo(it)
        rv.adapter = DataAdapter(
            this,
            l,
            savedInstanceState.getString("RVWBreed", "")
        )
        rv.scrollToPosition(
            savedInstanceState.getInt("RVENumber", 0)
        )

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        val lst = mutableListOf<PhotoDTO>()
        for (it in (rv.adapter as DataAdapter).list.orEmpty())
            lst += it.toDTO()
        outState.apply {
            putInt("RVENumber", rv.scrollState)
            putString("RVWBreed", (rv.adapter as DataAdapter).breed)
            putString(
                "SearchField",
                findViewById<Spinner>(R.id.breeds_selector).selectedItem.toString()
            )
            putString("Imgs", GsonBuilder().create().toJson(lst))
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.gotoLiked){
            val i = Intent(this, LastTenVotedActivity::class.java)
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)
    }

    class DataAdapter(private val context: AppCompatActivity,
                      var list: List<Photo>?,
                      val breed: String
    ) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)

        private fun load(){
            Thread{
                val dtos = (NetworkService.retrofit
                    .getPhotoForBreed(
                        breed, 100, null,
                        itemCount / 100 + 1
                    )
                    ?.execute()?.body()).orEmpty().filterNotNull()
                val photos: MutableList<Photo> = mutableListOf()
                for(it in dtos)
                    photos += Photo(it)

                list = list?.plus(
                    photos
                )
                context.runOnUiThread {
                    notifyDataSetChanged()
                }
            }.start()
        }

        init {
            if (list == null){
                list = listOf()
                load()
            }
        }

        private fun changeLikeState(position:Int, holder: ViewHolder, likeState: Int){
            val photo = list.orEmpty()[position]
            when {
                photo.liked == -1 || photo.liked != likeState  -> {
                    NetworkService.vote(photo.id, likeState)
                    photo.liked = likeState
                }
                photo.liked != -1 && photo.liked == likeState -> {
                    NetworkService.delVote(photo.id)
                    photo.liked = -1
                }
            }
            context.runOnUiThread{notifyDataSetChanged()}
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.recycler_view_element, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = list?.size ?: 0

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == (list?.size?:0) - 5) load()
            val l = list.orEmpty()
            Thread{
                l[position].attachTo(holder.img)
            }.start()
            when (l[position].liked){
                0 -> {
                    holder.like.setImageDrawable(context.getDrawable(R.drawable.like))
                    holder.dis.setImageDrawable(context.getDrawable(R.drawable.dislike_placed))
                }
                1 -> {
                    holder.like.setImageDrawable(context.getDrawable(R.drawable.like_placed))
                    holder.dis.setImageDrawable(context.getDrawable(R.drawable.dislike))
                }
                else -> {
                    holder.like.setImageDrawable(context.getDrawable(R.drawable.like))
                    holder.dis.setImageDrawable(context.getDrawable(R.drawable.dislike))
                }
            }
            holder.like.setOnClickListener{
                changeLikeState(position, holder, 1)
            }
            holder.dis.setOnClickListener{
                changeLikeState(position, holder, 0)
            }
        }

        class ViewHolder(view: View):RecyclerView.ViewHolder(view){
            val img: ImageView = view.findViewById(R.id.img)
            val like: ImageButton = view.findViewById(R.id.btn_like)
            val dis: ImageButton = view.findViewById(R.id.btn_dislike)
        }
    }
}
