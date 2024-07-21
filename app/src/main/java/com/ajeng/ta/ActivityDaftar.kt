package com.ajeng.ta

import  android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ActivityDaftar : AppCompatActivity() {
    private lateinit var txNama: TextInputEditText
    private lateinit var txAsalSekolah: TextInputEditText
    //private lateinit var txKelas: TextInputEditText
    private lateinit var spnLayanan: Spinner
    private lateinit var spnAsalSekolah: Spinner
    private lateinit var btnDaftar: Button
    private lateinit var btnAnak: Button
    private lateinit var iconDaftar: ImageView
    private lateinit var cardView6: CardView
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }
    private var responseBody: String? = null
    private val client = OkHttpClient()

    private val layananMap = mutableMapOf<String, String>() // Map to store id_layanan and nama_layanan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)
        supportActionBar?.hide()

        responseBody = sharedPreferences.getString("responseBody", null)
        if (responseBody == null) {
            responseBody = intent.getStringExtra("responseBody")
        }

        var idUser: String? = null
        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            Log.e("ActivityDaftar", "Data : $jsonResponse")
            idUser = jsonResponse.getJSONObject("data").optString("ID")
        } else {
            Log.e("ActivityDaftar", "responseBody is null")
            Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        btnDaftar = findViewById(R.id.btnDaftar)
        btnAnak = findViewById(R.id.btnAnak)
        txNama = findViewById(R.id.txUsn)
        txAsalSekolah = findViewById(R.id.txAsal)
        //txKelas = findViewById(R.id.txKls)
        spnLayanan = findViewById(R.id.spnLayanan)
        spnAsalSekolah = findViewById(R.id.spnAsalSekolah)
        iconDaftar = findViewById(R.id.icondaftar)
        cardView6 = findViewById(R.id.cardView6)

        // Set gambar default untuk ivShowHidePwd
        iconDaftar.setImageResource(R.drawable.icon_daftar)

        // Load layanan data
        loadLayananData()

        // Set options for spnAsalSekolah
        setAsalSekolahOptions()

        spnAsalSekolah.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == "Lainnya") {
                    cardView6.visibility = View.VISIBLE
                } else {
                    cardView6.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Tidak ada aksi yang diperlukan
            }
        }

        btnDaftar.setOnClickListener {
            val nama = txNama.text.toString().trim()
            val asalSekolah = if (spnAsalSekolah.selectedItem.toString() == "Lainnya") {
                txAsalSekolah.text.toString().trim()
            } else {
                spnAsalSekolah.selectedItem.toString()
            }
            //val kelas = txKelas.text.toString().trim()
            val namaLayanan = spnLayanan.selectedItem?.toString()?.trim() ?: ""

            if (nama.isEmpty() || asalSekolah.isEmpty() || namaLayanan.isEmpty()) {
                Toast.makeText(this@ActivityDaftar, "Mohon isi semua data.", Toast.LENGTH_SHORT).show()
            } else {
                val idUserVal = idUser
                if (idUserVal != null) {
                    val idLayanan = layananMap[namaLayanan] ?: ""
                    // Log nilai sebelum mengirim permintaan
                    Log.d("ActivityDaftar", "Nama: $nama")
                    Log.d("ActivityDaftar", "AsalSekolah: $asalSekolah")
                    //Log.d("ActivityDaftar", "Kelas: $kelas")
                    Log.d("ActivityDaftar", "Layanan: $idLayanan")
                    Log.d("ActivityDaftar", "ID User: $idUserVal")

                    val daftarRequest = DaftarRequest(nama, asalSekolah, idLayanan, idUserVal)
                    daftar(daftarRequest)
                } else {
                    Toast.makeText(this@ActivityDaftar, "ID User tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnAnak.setOnClickListener {
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
            val intent = Intent(this@ActivityDaftar, ActivityAnak::class.java)
            intent.putExtra("responseBody", responseBody)
            startActivity(intent)
        }
    }


    private fun setAsalSekolahOptions() {
        val asalSekolahList = listOf("SD Negeri 1", "SD Negeri 2", "SMP Negeri 3", "Lainnya")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, asalSekolahList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnAsalSekolah.adapter = adapter
    }

    private fun loadLayananData() {
        val request = Request.Builder()
            .url("http:192.168.0.56:8081/TA_1/api/Manage_all/get_layanan")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ActivityDaftar, "Gagal memuat data layanan. ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        val layananList = parseLayananResponse(responseBody)
                        val adapter = ArrayAdapter(this@ActivityDaftar, android.R.layout.simple_spinner_item, layananList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spnLayanan.adapter = adapter
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ActivityDaftar, "Gagal memuat data layanan. ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun parseLayananResponse(responseBody: String?): List<String> {
        val layananList = mutableListOf<String>()
        if (responseBody != null) {
            val jsonArray = JSONArray(responseBody)
            for (i in 0 until jsonArray.length()) {
                val layanan = jsonArray.getJSONObject(i)
                val idLayanan = layanan.getString("id_layanan")
                val namaLayanan = layanan.getString("nama_layanan")
                layananMap[namaLayanan] = idLayanan // Store id_layanan and nama_layanan in the map
                layananList.add(namaLayanan)
            }
        }
        return layananList
    }

    private fun daftar(daftarRequest: DaftarRequest) {
        val formBody = FormBody.Builder()
            .add("nama", daftarRequest.nama)
            .add("asal_sekolah", daftarRequest.asal_sekolah)
//            .add("kelas", daftarRequest.kelas)
            .add("id_layanan", daftarRequest.id_layanan)
            .add("id_user", daftarRequest.idUser)
            .build()

        val request = Request.Builder()
            .url("http:192.168.0.56:8081/TA_1/api/Manage_all/daftar")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ActivityDaftar, "Pendaftaran gagal. Terjadi kesalahan jaringan. ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        Toast.makeText(this@ActivityDaftar, responseBody ?: "Pendaftaran berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ActivityDaftar, HomeActivity::class.java))
                        finish()
                    }
                } else {
//                    val errorMessage = response.body?.string() ?: "Unknown error"
//                    val statusCode = response.code
                    runOnUiThread {
                        Toast.makeText(this@ActivityDaftar, "Pendaftaran gagal. Anda sudah terdaftar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    data class DaftarRequest(
        val nama: String,
        val asal_sekolah: String,
//        val kelas: String,
        val id_layanan: String,
        val idUser: String
    )

//    data class DaftarResponse(
//        val message: String
//    )
}
