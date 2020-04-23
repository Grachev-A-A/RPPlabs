package ru.mirea.ikbo1218.grachev.lab5.data

import retrofit2.Call
import retrofit2.http.*


interface CatApi {
    companion object{
        const val key = "x-api-key: 58a48431-0429-4fc6-8e66-6c06b24137da"
        const val sub_id = "MIREA_RPP_lab5_Grachev"
    }

    @Headers(key)
    @GET("images/{image_id}")
    fun getImgById(
        @Path("image_id") imageId: String
    ): Call<PhotoDTO?>

    @Headers(key)
    @GET("breeds")
    fun getBreeds(): Call<List<BreedDTO?>?>?

    @Headers(key)
    @GET("images/search?mime_types=gif,jpg,png")
    fun getPhotoForBreed(
        @Query("breed_ids") breed: String?,
        @Query("limit") limit: Int,
        @Query("order") desc: String?,
        @Query("page") page: Int
    ): Call<List<PhotoDTO?>?>?

    @Headers(key)
    @GET("votes")
    fun getVotes(
        @Query("sub_id") sub_id: String = CatApi.sub_id
    ): Call<List<Vote?>?>?

    @Headers(key)
    @GET("votes/{vote_id}")
    fun getVoteById(
        @Path("vote_id") voteId: String
    ): Call<Vote?>?

    @Headers(key)
    @DELETE("votes/{vote_id}")
    fun delVote(
        @Path("vote_id") vote_id: String
    ): Call<Void?>?

    @Headers(key)
    @POST("votes")
    fun voteForPic(@Body postCreate: VoteCreate): Call<Vote?>?
}