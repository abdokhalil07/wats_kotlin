package com.example.wtascopilot.data.remote


import com.example.wtascopilot.data.remote.model.ApiResponse
import com.example.wtascopilot.data.remote.model.TransactionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("transactions/add")
    suspend fun sendTransaction(
        @Body request: TransactionRequest
    ): Response<ApiResponse>
}