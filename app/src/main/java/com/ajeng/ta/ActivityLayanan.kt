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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActivityLayanan : AppCompatActivity() {
    lateinit var adapter: AdapterLayananTentor
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan)
        supportActionBar?.hide()

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.listLayanan)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterLayananTentor(arrayListOf(), this)
        recyclerView.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        getLayanan(apiService)
    }

    private fun getLayanan(apiService: ApiService) {
        val call = apiService.getLayanan()
        call.enqueue(object : Callback<ArrayList<ListLayanan>> {
            override fun onResponse(call: Call<ArrayList<ListLayanan>>, response: Response<ArrayList<ListLayanan>>) {
                if (response.isSuccessful) {
                    showNoLayananMessage(false)
                    val layananList = response.body()
                    if (layananList != null) {
                        adapter.setData(layananList)
                    }
                } else {
                    showNoLayananMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data layanan: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListLayanan>>, t: Throwable) {
                Log.e("Dashboard recycle", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun showNoLayananMessage(show: Boolean) {
        val recyclerViewLayanan = findViewById<RecyclerView>(R.id.listLayanan)
        recyclerViewLayanan.visibility = if (show) View.GONE else View.VISIBLE
    }
}