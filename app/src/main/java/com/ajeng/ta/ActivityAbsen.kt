package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajeng.ta.databinding.ActivityAbsenBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ActivityAbsen : AppCompatActivity() {
    private lateinit var iconAbsen: ImageView
    private lateinit var binding: ActivityAbsenBinding
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }
    private var responseBody: String? = null
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        responseBody = sharedPreferences.getString("responseBody", null)
        if (responseBody == null) {
            responseBody = intent.getStringExtra("responseBody")
        }

        var idUser: String? = null
        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            Log.e("ActivityAbsen", "Data : $jsonResponse")
            idUser = jsonResponse.getJSONObject("data").optString("ID")
        } else {
            Log.e("ActivityAbsen", "responseBody is null")
            Toast.makeText(this, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        // Set the current date to txTanggal
        val currentDate = getCurrentDate()
        binding.txTanggal.setText(currentDate)

        // Set the current time to txJam
        val currentTime = getCurrentTime()
        binding.txJam.setText(currentTime)

        iconAbsen = findViewById(R.id.iconabsen)
        iconAbsen.setImageResource(R.drawable.icon_absen)

        binding.btnAbsen.setOnClickListener {
            val tanggal = binding.txTanggal.text.toString().trim()
            val jam = binding.txJam.text.toString().trim()
            val materi = binding.txMateri.text.toString().trim()

            if (tanggal.isEmpty() || materi.isEmpty()) {
                Toast.makeText(this@ActivityAbsen, "Mohon isi semua data.", Toast.LENGTH_SHORT).show()
            } else {
                val idUserVal = idUser
                if (idUserVal != null) {
                    Log.d("ActivityAbsen", "Tanggal: $tanggal")
                    Log.d("ActivityAbsen", "Jam: $jam")
                    Log.d("ActivityAbsen", "Materi: $materi")
                    Log.d("ActivityAbsen", "ID User: $idUserVal")

                    val absenRequest = AbsenRequest(tanggal, jam, materi, idUserVal)
                    absen(absenRequest)
                } else {
                    Toast.makeText(this@ActivityAbsen, "ID User tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(Calendar.getInstance().time)
    }

    private fun absen(absenRequest: AbsenRequest) {
        val formBody = FormBody.Builder()
            .add("tanggal", absenRequest.tanggal)
            .add("jam", absenRequest.jam)
            .add("materi", absenRequest.materi)
            .add("id_user", absenRequest.idUser)
            .build()

        val request = Request.Builder()
            .url("http://192.168.0.56:8081/TA_1/kelola_absen")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ActivityAbsen, "Absensi gagal. Terjadi kesalahan jaringan. ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        Toast.makeText(this@ActivityAbsen, responseBody ?: "Absen berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ActivityAbsen, HomeTentorActivity::class.java))
                        finish()
                    }
                } else {
                    val errorMessage = response.body?.string() ?: "Unknown error"
                    val statusCode = response.code
                    runOnUiThread {
                        Toast.makeText(this@ActivityAbsen, "Absen gagal. $statusCode - $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    data class AbsenRequest(
        val tanggal: String,
        val jam: String,
        val materi: String,
        val idUser: String
    )
}
