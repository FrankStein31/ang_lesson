package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
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

class HomeActivity : AppCompatActivity() {
    //    lateinit var iconLayanan: ImageView
//    lateinit var iconJadwal: ImageView
    lateinit var iconDaftar: ImageView
    lateinit var iconTagihan: ImageView
    lateinit var iconRiwayat: ImageView
    lateinit var icon: ImageView
    lateinit var adapter2: TentorAdapter
    lateinit var adapter: LayananAdapter
    lateinit var fragProfil: FragmentProfil
    lateinit var ft: FragmentTransaction
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        val daftar: CardView = findViewById(R.id.clDaftar)
        val btmNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val frm: FrameLayout = findViewById(R.id.frameLayout)
//        val layanan: CardView = findViewById(R.id.clLayanan)
//        val jadwal: CardView = findViewById(R.id.clJadwal)
        val tagihan: CardView = findViewById(R.id.clTagihan)
        val riwayat: CardView = findViewById(R.id.clRiwayat)
        fragProfil = FragmentProfil()
        iconDaftar = findViewById(R.id.icondaftar)
        icon = findViewById(R.id.Icon)
//        iconLayanan = findViewById(R.id.iconLayanan)
//        iconJadwal = findViewById(R.id.iconJadwal)
        iconTagihan = findViewById(R.id.icontagihan)
        iconRiwayat = findViewById(R.id.iconriwayat)

//        iconLayanan.setImageResource(R.drawable.list)
//        iconJadwal.setImageResource(R.drawable.calendar2)
        iconTagihan.setImageResource(R.drawable.tagihan)
        iconRiwayat.setImageResource(R.drawable.history)
        iconDaftar.setImageResource(R.drawable.daftar)
        icon.setImageResource(R.drawable.logo_baru)

//        layanan.setOnClickListener{
//            val intent = Intent(this@HomeActivity, ActivityLayanan::class.java)
//            startActivity(intent)
//        }
//
//        jadwal.setOnClickListener{
//            val intent = Intent(this@HomeActivity, ActivityJadwal::class.java)
//            startActivity(intent)
//        }

        tagihan.setOnClickListener{
            val intent = Intent(this@HomeActivity, ActivityTagihan::class.java)
            startActivity(intent)
        }
        riwayat.setOnClickListener{
            val intent = Intent(this@HomeActivity, ActivityRiwayat::class.java)
            startActivity(intent)
        }

        daftar.setOnClickListener{
            val intent = Intent(this@HomeActivity, ActivityDaftar::class.java)
            startActivity(intent)
        }

        //ambil data dari login
        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            Log.e("Home Activity", "Data : $jsonResponse")
        } else {daftar.setOnClickListener {
            if (!isLoggedIn()) {
                Toast.makeText(this@HomeActivity, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                responseBody = intent.getStringExtra("responseBody")
                if (responseBody == null) {
                    responseBody = sharedPreferences.getString("responseBody", null)
                }
                if (responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    Log.e("Data Daftar", "Data : $jsonResponse")
                } else {
                    Log.e("Data Daftar", "responseBody is null")
                    // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
                    Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this@HomeActivity, ActivityDaftar::class.java)
                intent.putExtra("responseBody", responseBody)
                startActivity(intent)
            }
        }
            Log.e("Home Activity", "responseBody is null")
            // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
            Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }



        btmNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemHome -> {
                    responseBody = intent.getStringExtra("responseBody")
                    if (responseBody == null) {
                        responseBody = sharedPreferences.getString("responseBody", null)
                    } else if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        Log.e("item home", "Data : $jsonResponse")
                    } else {
                        Log.e("item home", "responseBody is null")
                        // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
                        Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT)
                            .show()
                    }
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("responseBody", responseBody)
                    startActivity(intent)
                }
                R.id.itemProfil -> {
                    if (!isLoggedIn()) {
                        Toast.makeText(this@HomeActivity, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show()
                    } else {
                        responseBody = intent.getStringExtra("responseBody")
                        if (responseBody == null) {
                            responseBody = sharedPreferences.getString("responseBody", null)
                        }
                        else if (responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            Log.e("item profile", "Data : $jsonResponse")
                        } else {
                            Log.e("item profile", "responseBody is null")
                            // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
                            Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                        intent.putExtra("responseBody", responseBody)
                        ft = supportFragmentManager.beginTransaction()
                        ft.replace(R.id.frameLayout, fragProfil)
                        ft.commit()
                        frm.visibility = View.VISIBLE
                        daftar.visibility = View.GONE
                        tagihan.visibility = View.GONE
                        riwayat.visibility = View.GONE
//                        layanan.visibility = View.GONE
//                        jadwal.visibility = View.GONE
                    }
                }
            }
            true
        }

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
            .baseUrl("http:192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService2 = retrofit2.create(ApiService::class.java)

        getTentor(apiService2)

        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/api/Manage_all/get_layanan/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        //getLayanan(apiService)
        getLayanan(apiService)


    }

    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
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