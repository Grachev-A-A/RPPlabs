package ru.mirea.ikbo1218.grachev.lab5.data

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    val retrofit: CatApi = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CatApi::class.java)

    private var votes: List<Vote?>? = listOf()
    private var breeds: List<BreedDTO?>? = listOf()

    fun getBreedId(name: String): String = breeds.orEmpty().find { it?.name == name }?.id ?: ""

    fun getBreeds(): Array<String> {
        val l = mutableListOf<String>()
        for (item  in breeds.orEmpty().filterNotNull())
            l += item.name
        return l.toTypedArray()
    }

    init {
        votes = retrofit
            .getVotes()
            ?.execute()?.body()
        breeds = retrofit
            .getBreeds()
            ?.execute()?.body()
    }

    fun vote(picId: String, value: Int){
        retrofit.voteForPic(VoteCreate(picId, value))
            ?.enqueue(object: Callback<Vote?>{
                override fun onFailure(call: Call<Vote?>, t: Throwable) {
                    Log.e("NetworkError", t.toString())
                }

                override fun onResponse(call: Call<Vote?>, response: Response<Vote?>) {
                    votes?.find { it?.image_id == picId }?.let {
                        votes = votes?.minus(it)
                    }

                    retrofit.getVoteById(response.body()?.voteId!!)?.enqueue(object : Callback<Vote?>{
                        override fun onFailure(call: Call<Vote?>, t: Throwable) {
                            Log.e("NetworkError", t.toString())
                        }

                        override fun onResponse(call: Call<Vote?>, response: Response<Vote?>) {
                            votes = votes?.plus(response.body())
                        }

                    })
                }
            })
    }

    fun delVote (picId:String) {
        votes.orEmpty().find { it?.image_id == picId }?.apply {
            votes = votes?.minus(this)
            retrofit.delVote(voteId)
                ?.enqueue(object : Callback<Void?> {
                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        Log.e("NetworkError", t.toString())
                    }
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {}
                })
        }
    }


    fun lastTenLikes(): Array<Vote?>{
        val a = Array<Vote?>(10){null}
        var cnt = 0
        for (it in votes.orEmpty().filterNotNull().reversed()){
            if(it.value == 1 && cnt < a.size)
                a[cnt++] = it
        }
        return a
    }

    fun voteValue(pictureId: String): Int
            = votes.orEmpty().find { it?.image_id == pictureId }?.value ?: -1

}