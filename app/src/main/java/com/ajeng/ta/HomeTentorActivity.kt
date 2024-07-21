package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Calendar

class HomeTentorActivity : AppCompatActivity() {
    lateinit var iconAbsen: ImageView
    lateinit var iconJadwal: ImageView
    lateinit var icon: ImageView
    lateinit var adapter: RekapAdapter
    lateinit var fragProfilTentor: FragmentProfilTentor
    lateinit var ft: FragmentTransaction
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

//    interface ApiService {
//        @GET("absen/{id_user}")
//        fun getRekapById(@Path("id_user") id_user: String): Call<ArrayList<ListRekap>>
//    }
    interface ApiService {
        @GET("absen/{id_user}")
        fun getRekapById(
            @Path("id_user") id_user: String
        ): Call<ArrayList<ListRekap>>

        @GET("absen/{id_user}/{tanggal}/{bulan}/{tahun}")
        fun getRekapByIdFiltered(
            @Path("id_user") id_user: String,
            @Path("tanggal") tanggal: String,
            @Path("bulan") bulan: String,
            @Path("tahun") tahun: String
        ): Call<ArrayList<ListRekap>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tentor)
        supportActionBar?.hide()

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        val absen: CardView = findViewById(R.id.clAbsen)
        val filter: Button = findViewById(R.id.btn_filter)
        val jadwal: CardView = findViewById(R.id.clJadwal)
        val btmNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val frm: FrameLayout = findViewById(R.id.frameLayout)
        fragProfilTentor = FragmentProfilTentor()
        iconAbsen = findViewById(R.id.iconabsen)
        iconJadwal = findViewById(R.id.iconjadwal)
        icon = findViewById(R.id.icon)

        iconAbsen.setImageResource(R.drawable.add)
        iconJadwal.setImageResource(R.drawable.calendar)
        icon.setImageResource(R.drawable.logo_baru)

        //ambil data dari login
        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            Log.e("Home Activity Tentor", "Data : $jsonResponse")
            val data = jsonResponse.getJSONObject("data")
            val id_user = data.getString("ID")
            Log.e("ID Home Activity Tentor", "ID USER : $id_user")
        } else {
            Log.e("Home Activity Tentor", "responseBody is null")
            // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
            Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        absen.setOnClickListener {
            responseBody = intent.getStringExtra("responseBody")
            if (responseBody == null) {
                responseBody = sharedPreferences.getString("responseBody", null)
            }
            if (responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                Log.e("Data Absen", "Data : $jsonResponse")
            } else {
                Log.e("Data Absen", "responseBody is null")
                // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
                Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
            val intent = Intent(this@HomeTentorActivity, ActivityAbsen::class.java)
            intent.putExtra("responseBody", responseBody)
            startActivity(intent)
        }

        filter.setOnClickListener {
            showFilterBottomSheet()
        }

        jadwal.setOnClickListener{
            val intent = Intent(this@HomeTentorActivity, ActivityLayananTentor::class.java)
            startActivity(intent)
        }

        btmNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemHomeTentor -> {
                    responseBody = intent.getStringExtra("responseBody")
                    if (responseBody == null) {
                        responseBody = sharedPreferences.getString("responseBody", null)
                    } else if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        Log.e("item home tentor", "Data : $jsonResponse")
                    } else {
                        Log.e("item home tentor", "responseBody is null")
                        // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
                        Toast.makeText(this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT)
                            .show()
                    }
                    val intent = Intent(this, HomeTentorActivity::class.java)
                    intent.putExtra("responseBody", responseBody)
                    startActivity(intent)
                }
                R.id.itemProfilTentor -> {
                    responseBody = intent.getStringExtra("responseBody")
                    if (responseBody == null) {
                        responseBody = sharedPreferences.getString("responseBody", null)
                    } else if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        Log.e("item profile tentor", "Data : $jsonResponse")
                    } else {
                        Log.e("item profil tentor", "responseBody is null")
                        // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
                        Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT)
                            .show()
                    }
                    intent.putExtra("responseBody", responseBody)
                    ft = supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frameLayout, fragProfilTentor)
                    ft.commit()
                    frm.visibility = View.VISIBLE
                    absen.visibility = View.GONE
                    jadwal.visibility = View.GONE
                    filter.visibility = View.GONE
                }
            }
            true
        }

        // Pengaturan RecyclerView untuk rekap
        val recyclerView = findViewById<RecyclerView>(R.id.listRiwayat)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RekapAdapter(arrayListOf(), this)
        recyclerView.adapter = adapter

        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            val data = jsonResponse.getJSONObject("data")
            val id_user = data.getString("ID")
            Log.e("ID", "ID USER : $id_user")
            getRekap(id_user)
        } else {
            responseBody = sharedPreferences.getString("responseBody", null)
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_filter, null)

        val spinnerDate: Spinner = view.findViewById(R.id.spinner_date)
        val spinnerMonth: Spinner = view.findViewById(R.id.spinner_month)
        val spinnerYear: Spinner = view.findViewById(R.id.spinner_year)
        val applyButton: Button = view.findViewById(R.id.btn_apply_filter)

        // Get current month and year
        val calendar = Calendar.getInstance()
        val currentDate = calendar.get(Calendar.DATE)
        val currentMonth = calendar.get(Calendar.MONTH) // Spinners usually start from 1
        val currentYear = calendar.get(Calendar.YEAR)

        // Set default values for the spinners
        spinnerDate.setSelection(currentDate - 1) // Spinner indexing starts from 0
        spinnerMonth.setSelection(currentMonth) // Spinner indexing starts from 0

        // Find the position of the current year in the years array
        val yearsArray = resources.getStringArray(R.array.years_array)
        val currentYearPosition = yearsArray.indexOf(currentYear.toString())
        if (currentYearPosition >= 0) {
            spinnerYear.setSelection(currentYearPosition) // Set to the current year
        }

        applyButton.setOnClickListener {
            val selectedDate = spinnerDate.selectedItem.toString()
            val selectedMonth = spinnerMonth.selectedItem.toString()
            val selectedYear = spinnerYear.selectedItem.toString()
            val jsonResponse = JSONObject(responseBody)
            val data = jsonResponse.getJSONObject("data")
            val id_user = data.getString("ID")
            if (id_user != null) {
                Log.e("Get Rekap filter", "Tanggal : $selectedDate, Bulan : $selectedMonth, Tahun : $selectedYear")
//                getRekap(id_user, selectedDate, selectedMonth, selectedYear)
                val formattedDate = String.format("%02d", selectedDate.toInt())
                val formattedMonth = String.format("%02d", selectedMonth.toInt())
                Log.e("Get Rekap filter format", "Tanggal : $formattedDate, Bulan : $formattedMonth, Tahun : $selectedYear")
                getRekap(id_user, formattedDate, formattedMonth, selectedYear)
            }

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun getRekap(id_user: String, tanggal: String? = null, bulan: String? = null, tahun: String? = null) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val call = if (tanggal != null && bulan != null && tahun != null) {
            val url = "http://192.168.0.56:8081/TA_1/absen/$id_user/$tanggal/$bulan/$tahun"
            Log.e("API Call", "URL: $url")
            apiService.getRekapByIdFiltered(id_user, tanggal, bulan, tahun)
        } else {
            val url = "http://192.168.0.56:8081/TA_1/absen/$id_user"
            Log.e("API Call", "URL: $url")
            apiService.getRekapById(id_user)
        }

        call.enqueue(object : Callback<ArrayList<ListRekap>> {
            override fun onResponse(call: Call<ArrayList<ListRekap>>, response: Response<ArrayList<ListRekap>>) {
                Log.e("API Response", "Code: ${response.code()}, Message: ${response.message()}")
                if (response.isSuccessful) {
                    val rekapList = response.body()
                    if (rekapList != null && rekapList.isNotEmpty()) {
                        adapter.setData(rekapList)
                        showNoLayananMessage(false)
                    } else {
                        showNoLayananMessage(true)
                        Log.e("Dashboard", "No data available")
                    }
                } else {
                    showNoLayananMessage(true)
                    Log.e("Dashboard", "Failed to get rekap data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListRekap>>, t: Throwable) {
                showNoLayananMessage(true)
                Log.e("Dashboard", "API call failed", t)
            }
        })
    }

    private fun showNoLayananMessage(show: Boolean) {
        val recyclerViewRekap = findViewById<RecyclerView>(R.id.listRiwayat)
        recyclerViewRekap.visibility = if (show) View.GONE else View.VISIBLE
    }
}