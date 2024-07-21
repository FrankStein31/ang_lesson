package com.ajeng.ta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajeng.ta.services.ApiService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ActivityRiwayat : AppCompatActivity() {
    lateinit var adapter: RiwayatPembayaranAdapter
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

    interface ApiService {
        @GET("pembayaran_json/{id_user}")
        fun getRiwayatPembayaranById(@Path("id_user") id_user: String): Call<ArrayList<ListRiwayatPembayaran>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_pembayaran)
        supportActionBar?.hide()

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.listRiwayat)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RiwayatPembayaranAdapter(arrayListOf(), this)
        recyclerView.adapter = adapter

        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            val data = jsonResponse.getJSONObject("data")
            val id_user = data.getString("ID")
            Log.e("ID", "ID USER : $id_user")
            getRiwayatPembayaran(id_user)
        } else {
            responseBody = sharedPreferences.getString("responseBody", null)
        }
    }

    private fun getRiwayatPembayaran(id_user: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getRiwayatPembayaranById(id_user)

        call.enqueue(object : Callback<ArrayList<ListRiwayatPembayaran>> {
            override fun onResponse(call: Call<ArrayList<ListRiwayatPembayaran>>, response: Response<ArrayList<ListRiwayatPembayaran>>) {
                if (response.isSuccessful) {
                    showNoRiwayatPembayaranMessage(false)
                    val RiwayatPembayaranList = response.body()
                    if (RiwayatPembayaranList != null && RiwayatPembayaranList.isNotEmpty()) {
                        adapter.setData(RiwayatPembayaranList)
                    } else {
                        // Handle case when rekapList is empty
                        showNoRiwayatPembayaranMessage(true)
                        Log.e("Dashboard error sukses", "Gagal mendapatkan data rekap: ${response.message()}")
                    }
                } else {
                    showNoRiwayatPembayaranMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data rekap: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListRiwayatPembayaran>>, t: Throwable) {
                showNoRiwayatPembayaranMessage(true)
                Log.e("Dashboard", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun showNoRiwayatPembayaranMessage(show: Boolean) {
        val recyclerViewRiwayatPembayaran = findViewById<RecyclerView>(R.id.listRiwayat)
        recyclerViewRiwayatPembayaran.visibility = if (show) View.GONE else View.VISIBLE
    }
}