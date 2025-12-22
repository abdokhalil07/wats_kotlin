package com.example.wtascopilot.api


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Headers

interface ApiService {

    @GET("userinfo")
    suspend fun getPosts(): Response<List<PostModel>>

    @POST("posts")
    suspend fun createPost(
        @Body post: PostModel
    ): Response<PostModel>

    @GET("posts")
    suspend fun searchPosts(
        @Query("userId") id: Int,
        @Query("title") keyword: String
    ): Response<List<PostModel>>


    @Headers("Content-Type: application/json")
    @POST("userinfo/byusername")
    suspend fun getPostsByUserId(
        @Body request: UserSearchDto // ده هيتحول في الرابط لـ posts?userId=id
    ): Response<PostModel>
}

