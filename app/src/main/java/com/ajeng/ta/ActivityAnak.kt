package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ActivityAnak : AppCompatActivity() {
    lateinit var adapter: AnakAdapter
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

    interface ApiService {
        @GET("murid/{id_user}")
        fun getMuridById(@Path("id_user") id_user: String): Call<ArrayList<ListMurid>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anak)
        supportActionBar?.hide()

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            Log.e("Activity Layanan Tentor", "Data : $jsonResponse")
            val data = jsonResponse.getJSONObject("data")
            val id_user = data.getString("ID")
            Log.e("ID Home Activity Tentor", "ID USER : $id_user")
            getMuridId(id_user)
        } else {
            Log.e("Home Activity Tentor", "responseBody is null")
            Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.listAnak)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AnakAdapter(arrayListOf()) { id_murid ->
            val intent = Intent(this, ActivityDetailAnak::class.java)
            intent.putExtra("ID_MURID", id_murid)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun getMuridId(id_user: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getMuridById(id_user)

        call.enqueue(object : Callback<ArrayList<ListMurid>> {
            override fun onResponse(call: Call<ArrayList<ListMurid>>, response: Response<ArrayList<ListMurid>>) {
                if (response.isSuccessful) {
                    showNoMuridMessage(false)
                    val muridList = response.body()
                    if (muridList != null && muridList.isNotEmpty()) {
                        adapter.setData(muridList)
                    } else {
                        showNoMuridMessage(true)
                        Log.e("Dashboard error sukses", "Gagal mendapatkan data layanan: ${response.message()}")
                    }
                } else {
                    showNoMuridMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data rekap: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListMurid>>, t: Throwable) {
                showNoMuridMessage(true)
                Log.e("Dashboard", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun showNoMuridMessage(show: Boolean) {
        val recyclerViewLayanan = findViewById<RecyclerView>(R.id.listAnak)
        recyclerViewLayanan.visibility = if (show) View.GONE else View.VISIBLE
    }
}
