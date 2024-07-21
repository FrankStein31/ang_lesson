package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ActivityDetailAnak : AppCompatActivity() {
    lateinit var txNama: TextView
    lateinit var txLayanan: TextView
    lateinit var txAsal: TextView
    lateinit var txBiaya: TextView
    lateinit var btnKeluar: Button
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

//    interface ApiService {
//        @GET("detail_murid/{id_murid}")
//        fun getMuridByIdMurid(@Path("id_murid") id_murid: String?): Call<ListMurid>
//    }
    interface ApiService {
        @GET("detail_murid/{id_murid}")
        fun getMuridByIdMurid(@Path("id_murid") id_murid: String?): Call<List<ListMurid>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_anak)

        txNama = findViewById(R.id.txNama)
        txLayanan = findViewById(R.id.txLayanan)
        txAsal = findViewById(R.id.txAsal)
        txBiaya = findViewById(R.id.txBiaya)
        btnKeluar = findViewById(R.id.btnKeluar)

        val idMurid = intent.getStringExtra("ID_MURID")
        if (idMurid != null) {
            Log.e("ActivityDetailAnak", "ID MURID: $idMurid")
            fetchMuridDetail(idMurid)
        } else {
            Toast.makeText(this, "ID Murid tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
//        btnKeluar.setOnClickListener {
//            logout()
//        }
    }

    private fun fetchMuridDetail(id_murid: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getMuridByIdMurid(id_murid)

        call.enqueue(object : Callback<List<ListMurid>> {
            override fun onResponse(call: Call<List<ListMurid>>, response: Response<List<ListMurid>>) {
                if (response.isSuccessful) {
                    val muridList = response.body()
                    if (!muridList.isNullOrEmpty()) {
                        val murid = muridList[0] // Assuming you want the first item
                        Log.e("ActivityDetailAnak", "Response Body: $murid")
                        txNama.text = murid.nama
                        txLayanan.text = murid.nama_layanan
                        txAsal.text = murid.asal_sekolah
                        txBiaya.text = murid.biaya
                        Log.e("ActivityDetailAnak", "Data: $murid")
                    } else {
                        Log.e("ActivityDetailAnak", "Murid list is null or empty")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ActivityDetailAnak", "Response not successful: $errorBody")
                    Toast.makeText(this@ActivityDetailAnak, "Gagal mengambil data: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ListMurid>>, t: Throwable) {
                Log.e("ActivityDetailAnak", "Error fetching data", t)
                Toast.makeText(this@ActivityDetailAnak, "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun logout() {
//        with(sharedPreferences.edit()) {
//            clear()
//            putBoolean("isLoggedIn", false)
//            apply()
//        }

//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
}
