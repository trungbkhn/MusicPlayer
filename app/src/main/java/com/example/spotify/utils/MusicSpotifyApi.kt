package com.example.spotify.utils

import retrofit2.http.GET
import retrofit2.http.Query

interface MusicSpotifyApi {
//    search
//@GET("/3/search/movie")
//fun searchMovie(
//    @Query("api_key") key: String,
//    @Query("query") query: String,
//    @Query("page") page: Int
//): retrofit2.Call<MovieSearchResponse>
//
//    @GET("/3/movie/{movie_id}?")
//    fun getDetail(
//        @Path("movie_id") movie_id: Int,
//        @Query("api_key") key: String
//    ): retrofit2.Call<MovieModel>
//
//    @GET("/3/movie/popular")
//    fun getPopularMovie(
//        @Query("page") page: Int,
//        @Query("api_key") key: String
//    ): retrofit2.Call<MovieSearchResponse>
    @GET("/search")
    fun searchSong(
    @Query("rapidapi-key") key: String,
    @Query("query") query: String,
    @Query("type") type: String
    )

}