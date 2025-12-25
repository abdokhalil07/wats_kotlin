package com.example.wtascopilot.data.remote


import com.example.wtascopilot.api.PostModel
import com.example.wtascopilot.api.UserSearchDto
import com.example.wtascopilot.data.remote.model.ApiResponse
import com.example.wtascopilot.data.remote.model.LoginRequest
import com.example.wtascopilot.data.remote.model.LoginResponse
import com.example.wtascopilot.data.remote.model.SimAddRequest
import com.example.wtascopilot.data.remote.model.SimRequest
import com.example.wtascopilot.data.remote.model.SimStatusResponse
import com.example.wtascopilot.data.remote.model.TransactionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @POST("transactions/add")
    suspend fun sendTransaction(
        @Body request: TransactionRequest
    ): Response<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST("userinfo/byusername")
    suspend fun login(
        @Body request: LoginRequest // ده هيتحول في الرابط لـ posts?userId=id
    ): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("accountinfo/simstate")
    suspend fun checkSimStatus(@Body request: SimRequest): Response<SimStatusResponse>

    // 2. تسجيل شريحة جديدة
    @Headers("Content-Type: application/json")
    @POST("accountinfo/add")
    suspend fun addSim(@Body request: SimAddRequest): Response<SimStatusResponse>

    // 3. إيقاف (حذف) شريحة
    @POST("sims/remove")
    suspend fun removeSim(@Body request: SimRequest): Response<SimStatusResponse>
}