package com.ajeng.ta.services

import com.ajeng.ta.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

public interface ApiService {
//    @POST("api/Manage_all/login")
//    fun login(@Query("username") username: String, @Query("password") password: String): Call<LoginResponse>


    @POST("api/Manage_all/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("kelola_android")
    fun getLayanan(): Call<ArrayList<ListLayanan>>

    @GET("kelola_tentor_android")
    fun getTentor(): Call<ArrayList<ListTentor>>

    @GET("jadwal/{id_layanan}")
    fun getJadwalById(@Query("id_layanan") id_layanan: String?): Call<ArrayList<ListJadwal>>


    @GET("rekap/{id_user}")
    fun getRekapById(@Query("id_user") id_user: String): Call<ArrayList<ListRekap>>
    @GET("layanantentor/{id_user}")
    fun getLayananById2(@Path("id_user") id_user: String): Call<JsonArray>
    @GET("layanantentor/{id_user}")
    fun getLayananById(@Query("id_user") id_user: String): Call<ArrayList<ListLayanan>>

    @GET("murid/{id_user}")
    fun getMuridById(@Query("id_user") id_user: String): Call<ArrayList<ListMurid>>

    @GET("pembayaran_json/{id_user}")
    fun getRiwayatPembayaranById(@Query("id_user") id_user: String): Call<ArrayList<ListRiwayatPembayaran>>

    @POST("kelola_absen")
    @Multipart
    fun absen(
//        @Header("id_user") id_user: String,
        @Part("id_user") id_user: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part bukti: MultipartBody.Part?,
        @Part("materi") materi: RequestBody
    ): Call<DataDaftar>

//
//    @GET("getAllLayanan")
//    fun getAllLayanan(): Call<ArrayList<Layanan>>

//    @GET("get-all-pengaduan")
//    fun getAllPengaduan(): Call<ArrayList<ListPengadaan>>
//
//    @GET("/api/pengaduan/{nik}")
//    fun getPengaduanByNik(@Path("nik") nik: String): Call<ArrayList<ListPengadaan>>
//
//    @GET("/api/detail-pengaduan/{id}")
//    fun getDetailPengaduan(@Path("id") id: Int): Call<DetailPengadaan>
//
//    @PUT("/api/pengaduan/{id}/rating")
//    fun submitRating(@Path("id") id: Int, @Body ratingSubmit: RatingSubmit): Call<Void>
//
//    @POST("/api/create-pengaduan")
//    @Multipart
//    fun submitComplaint(
//        @Header("Authorization") token: String,
//        @Part("description") description: RequestBody,
//        @Part image: MultipartBody.Part?,
//        @Part("jenis_pengaduan") jenisPengaduan: RequestBody
//    ): Call<CreateKomplaint>
//
//    @GET("/api/tanggapan/{pengaduanId}")
//    fun getTanggapanByPengaduanId(@Path("pengaduanId") pengaduanId: Int): Call<List<Tanggapan>>
//
//    @GET("/api/profile/{nik}")
//    fun getUserProfile(@Path("nik") nik: String): Call<UserProfile>

}
