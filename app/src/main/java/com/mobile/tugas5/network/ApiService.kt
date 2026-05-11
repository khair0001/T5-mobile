package com.mobile.tugas5.network

import com.mobile.tugas5.model.LoginRequest
import com.mobile.tugas5.model.LoginResponse
import com.mobile.tugas5.model.Pasien
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("pasien")
    suspend fun getPasien(): Response<PasienResponse>

    @POST("pasien")
    suspend fun createPasien(
        @Body pasien: Pasien
    ): Response<PasienDetailResponse>

    @PUT("pasien/{id}")
    suspend fun updatePasien(
        @Path("id") id: Int,
        @Body pasien: Pasien
    ): Response<PasienDetailResponse>

    @DELETE("pasien/{id}")
    suspend fun deletePasien(
        @Path("id") id: Int
    ): Response<MessageResponse>
}

data class PasienResponse(
    val success: Boolean,
    val message: String,
    val data: List<Pasien>?
)

data class PasienDetailResponse(
    val success: Boolean,
    val message: String,
    val data: Pasien?
)

data class MessageResponse(
    val success: Boolean,
    val message: String
)