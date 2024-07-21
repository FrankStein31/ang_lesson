package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajeng.ta.services.ApiClient.apiService
import com.ajeng.ta.services.ApiService
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.Gopay
import com.midtrans.sdk.corekit.models.snap.Shopeepay
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityLayananTentor : AppCompatActivity() {
    lateinit var adapter: LayananAdapter
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

    interface ApiService {
        @GET("layanantentor/{id_user}")
        fun getLayananById(@Path("id_user") id_user: String): Call<ArrayList<ListLayanan>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layanan)
        supportActionBar?.hide()

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        //ambil data dari login
        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            Log.e("Activity Layanan Tentor", "Data : $jsonResponse")
            val data = jsonResponse.getJSONObject("data")
            val id_user = data.getString("ID")
            Log.e("ID Home Activity Tentor", "ID USER : $id_user")
        } else {
            Log.e("Home Activity Tentor", "responseBody is null")
            // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
            Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.listLayanan)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LayananAdapter(arrayListOf()) { id_layanan ->
            val intent = Intent(this, ActivityJadwal::class.java)
            intent.putExtra("ID_LAYANAN", id_layanan)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            val data = jsonResponse.getJSONObject("data")
            val id_user = data.getString("ID")
            Log.e("ID", "ID USER : $id_user")
            Log.e("Layanan Tentor", "Data : $jsonResponse")
            getLayananId(id_user)
        } else {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

    }

    private fun getLayananId(id_user: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getLayananById(id_user)

        call.enqueue(object : Callback<ArrayList<ListLayanan>> {
            override fun onResponse(call: Call<ArrayList<ListLayanan>>, response: Response<ArrayList<ListLayanan>>) {
                if (response.isSuccessful) {
                    showNoLayananMessage(false)
                    val layananList = response.body()
                    if (layananList != null && layananList.isNotEmpty()) {
                        adapter.setData(layananList)
                    } else {
                        // Handle case when rekapList is empty
                        showNoLayananMessage(true)
                        Log.e("Dashboard error sukses", "Gagal mendapatkan data layanan: ${response.message()}")
                    }
                } else {
                    showNoLayananMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data rekap: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListLayanan>>, t: Throwable) {
                showNoLayananMessage(true)
                Log.e("Dashboard", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun showNoLayananMessage(show: Boolean) {
        val recyclerViewLayanan = findViewById<RecyclerView>(R.id.listLayanan)
        recyclerViewLayanan.visibility = if (show) View.GONE else View.VISIBLE
    }
}
