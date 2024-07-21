package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajeng.ta.services.ApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
//    lateinit var iconLayanan: ImageView
//    lateinit var iconJadwal: ImageView
    lateinit var adapter2: TentorAdapter
    lateinit var adapter: LayananAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val btn_login: Button = findViewById(R.id.btn_login)
//        val layanan: CardView = findViewById(R.id.clLayanan)
//        val jadwal: CardView = findViewById(R.id.clJadwal)
//        iconLayanan = findViewById(R.id.iconLayanan)
//        iconJadwal = findViewById(R.id.iconJadwal)

//        iconLayanan.setImageResource(R.drawable.list)
//        iconJadwal.setImageResource(R.drawable.calendar2)

        // Set gambar default untuk

        btn_login.setOnClickListener {
            // Navigasi ke layar login (Buka komentar jika diperlukan)
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

//        layanan.setOnClickListener{
//            val intent = Intent(this@MainActivity, ActivityLayanan::class.java)
//            startActivity(intent)
//        }
//
//        jadwal.setOnClickListener{
//            val intent = Intent(this@MainActivity, ActivityJadwal::class.java)
//            startActivity(intent)
//        }

        val recyclerView2 = findViewById<RecyclerView>(R.id.listTentor)
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapter2 = TentorAdapter(arrayListOf(), this)
        recyclerView2.adapter = adapter2

        val recyclerView = findViewById<RecyclerView>(R.id.listLayanan)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LayananAdapter(arrayListOf()) { id_layanan ->
            val intent = Intent(this, ActivityJadwal::class.java)
            intent.putExtra("ID_LAYANAN", id_layanan)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Pengaturan Retrofit
        val retrofit2 = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/api/Manage_all/get_tentor/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService2 = retrofit2.create(ApiService::class.java)

        //getLayanan(apiService)
        getTentor(apiService2)

        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/api/Manage_all/get_layanan/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        //getLayanan(apiService)
        getLayanan(apiService)
    }

    private fun getTentor(apiService2: ApiService) {
        val call = apiService2.getTentor()
        call.enqueue(object : Callback<ArrayList<ListTentor>> {
            override fun onResponse(call: Call<ArrayList<ListTentor>>, response: Response<ArrayList<ListTentor>>) {
                if (response.isSuccessful) {
                    showNoTentorMessage(false)
                    val tentorList = response.body()
                    if (tentorList != null) {
                        adapter2.setData(tentorList)
                    }
                } else {
                    showNoTentorMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data tentor: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListTentor>>, t: Throwable) {
                Log.e("Dashboard recycle", "API gagal")
                t.printStackTrace()
            }
        })
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
                    Log.e("Dashboard", "Gagal mendapatkan data tentor: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListLayanan>>, t: Throwable) {
                Log.e("Dashboard recycle", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun showNoTentorMessage(show: Boolean) {
        val recyclerViewTentor = findViewById<RecyclerView>(R.id.listTentor)
        recyclerViewTentor.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showNoLayananMessage(show: Boolean) {
        val recyclerViewLayanan = findViewById<RecyclerView>(R.id.listLayanan)
        recyclerViewLayanan.visibility = if (show) View.GONE else View.VISIBLE
    }
}


