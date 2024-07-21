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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class ActivityJadwal : AppCompatActivity() {
    lateinit var adapter: JadwalAdapter
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

    interface ApiService {
        @GET("jadwal/{id_layanan}")
        fun getJadwalById(@Path("id_layanan") id_layanan: String?): Call<ArrayList<ListJadwal>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal)
        supportActionBar?.hide()

        val idLayanan = intent.getStringExtra("ID_LAYANAN")
        if (idLayanan != null) {
            Log.e("ActivityJadwal", "Received ID_LAYANAN: $idLayanan")
            getJadwal(idLayanan)
        } else {
            Log.e("ActivityJadwal", "ID_LAYANAN is null")
        }

        val recyclerView = findViewById<RecyclerView>(R.id.listJadwal)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = JadwalAdapter(arrayListOf(), this)
        recyclerView.adapter = adapter

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        if (responseBody != null) {
//            val jsonResponse = JSONObject(responseBody)
//            val data = jsonResponse.getJSONObject("data")
//            val id_layanan = data.getString("ID")
//            Log.e("ID", "ID USER : $id_layanan")
//            getJadwal(id_layanan)
            try {
                // Assume responseBody is the response you received
                val jsonResponse = JSONArray(responseBody)

                // Loop through the JSON array
                for (i in 0 until jsonResponse.length()) {
                    val data = jsonResponse.getJSONObject(i)
//                    val idLayanan = intent.getStringExtra("ID_LAYANAN")
//                    Log.e("ID_LAYANAN", "ID LAYANAN : $idLayanan")

                    // Call the function to get the schedule with id_layanan
                    val idLayanan = intent.getStringExtra("ID_LAYANAN")
                    if (idLayanan != null) {
                        Log.e("ActivityJadwal", "Received ID_LAYANAN: $idLayanan")
                        // Use the idLayanan here
                    } else {
                        Log.e("ActivityJadwal", "ID_LAYANAN is null")
                    }
                    getJadwal(idLayanan)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {
            responseBody = sharedPreferences.getString("responseBody", null)
        }
    }

    private fun getJadwal(idLayanan: String?) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getJadwalById(idLayanan)

        call.enqueue(object : Callback<ArrayList<ListJadwal>> {
            override fun onResponse(call: Call<ArrayList<ListJadwal>>, response: Response<ArrayList<ListJadwal>>) {
                if (response.isSuccessful) {
                    showNoJadwalMessage(false)
                    val JadwalList = response.body()
//                    if (JadwalList != null && JadwalList.isNotEmpty()) {
                    if (JadwalList != null && JadwalList.isNotEmpty()) {
                        adapter.setData(JadwalList)
                        Log.e("Dashboard sukses", "Berhasil data rekap: ${response.message()} || list : $JadwalList")
                    } else {
                        // Handle case when rekapList is empty
                        showNoJadwalMessage(true)
                        Log.e("Dashboard error sukses", "Gagal mendapatkan data rekap: ${response.message()} || list : $JadwalList")
                    }
                } else {
                    showNoJadwalMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data rekap: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListJadwal>>, t: Throwable) {
                showNoJadwalMessage(true)
                Log.e("Dashboard", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun showNoJadwalMessage(show: Boolean) {
        val recyclerViewJadwal = findViewById<RecyclerView>(R.id.listJadwal)
        recyclerViewJadwal.visibility = if (show) View.GONE else View.VISIBLE
    }
}