package com.example.louarayamacrame.data.network

import com.example.louarayamacrame.model.CustomRequest
import com.example.louarayamacrame.model.MacrameProduct
import com.example.louarayamacrame.model.UserProfile
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class LoginBody(val emailOrAlias: String, val password: String)

interface LouArayaApi {
    @POST("api/users/login")
    suspend fun login(@Body body: LoginBody): Response<UserProfile>

    @POST("api/users/register")
    suspend fun register(@Body user: UserProfile): Response<UserProfile>

    @GET("api/products")
    suspend fun getProducts(): List<MacrameProduct>

    @GET("api/requests/user/{userId}")
    suspend fun getRequestsByUser(@Path("userId") userId: String): List<CustomRequest>

    @POST("api/requests")
    suspend fun createRequest(@Body request: CustomRequest): CustomRequest
}

data class CatFactResponse(val fact: String, val length: Int)

interface ExternalApiService {
    @GET("fact")
    suspend fun getCatFact(): CatFactResponse
}

object ApiClient {
    // 10.0.2.2 points to localhost from Android Emulator
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private const val EXTERNAL_BASE_URL = "https://catfact.ninja/"

    val apiService: LouArayaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LouArayaApi::class.java)
    }

    val externalApiService: ExternalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(EXTERNAL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExternalApiService::class.java)
    }
}
