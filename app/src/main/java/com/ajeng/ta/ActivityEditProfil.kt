package com.ajeng.ta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajeng.ta.databinding.ActivityEditProfilBinding
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ActivityEditProfil : AppCompatActivity() {
    private lateinit var b: ActivityEditProfilBinding
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Dapatkan data yang tersimpan di SharedPreferences
        val responseBody = sharedPreferences.getString("responseBody", null)
        if (responseBody != null) {
            try {
                val jsonResponse = JSONObject(responseBody)
                if (jsonResponse.has("data")) {
                    val data = jsonResponse.getJSONObject("data")
                    val username = data.getString("username")
                    val alamat = data.getString("alamat")
                    val telepon = data.getString("telepon")
                    val email = data.getString("email")

                    b.txUsername.setText(username)
                    b.txAlamat.setText(alamat)
                    b.txTelepon.setText(telepon)
                    b.txEmail.setText(email)
                } else {
                    Log.e("Activity Edit", "Key 'data' not found in JSON")
                    Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Activity Edit", "Error parsing JSON", e)
                Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("Activity Edit", "responseBody is null")
            Toast.makeText(this, "Data profil tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        b.btnSimpan.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val updatedUsername = b.txUsername.text.toString().trim()
        val updatedAlamat = b.txAlamat.text.toString().trim()
        val updatedTelepon = b.txTelepon.text.toString().trim()
        val updatedEmail = b.txEmail.text.toString().trim()

        // Input validation
        if (updatedUsername.isEmpty() || updatedAlamat.isEmpty() || updatedTelepon.isEmpty() || updatedEmail.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val responseBody = sharedPreferences.getString("responseBody", null)
        var ID: String? = null
        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            if (jsonResponse.has("data")) {
                val data = jsonResponse.getJSONObject("data")
                ID = data.optString("ID")
            }
        }

        if (ID != null) {
            val urlBuilder = "http://192.168.0.56:8081/TA_1/api/Manage_all/edit_profil".toHttpUrlOrNull()?.newBuilder()

            if (urlBuilder != null) {
                val url = urlBuilder
                    .addQueryParameter("ID", ID)
                    .addQueryParameter("username", updatedUsername)
                    .addQueryParameter("alamat", updatedAlamat)
                    .addQueryParameter("telepon", updatedTelepon)
                    .addQueryParameter("email", updatedEmail)
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("ActivityEditProfil", "Gagal memperbarui profil", e)
                        runOnUiThread {
                            Toast.makeText(
                                this@ActivityEditProfil,
                                "Gagal memperbarui profil: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()
                        Log.d("ActivityEditProfil", "Response: $responseBody")

                        runOnUiThread {
                            if (response.isSuccessful) {
                                try {
                                    val jsonArray = JSONArray(responseBody)
                                    var updatedUser: JSONObject? = null

                                    // Find the updated user in the array
                                    for (i in 0 until jsonArray.length()) {
                                        val user = jsonArray.getJSONObject(i)
                                        if (user.getString("ID") == ID) {
                                            updatedUser = user
                                            break
                                        }
                                    }

                                    if (updatedUser != null) {
                                        // Update SharedPreferences with new data
                                        with(sharedPreferences.edit()) {
                                            putString("responseBody", JSONObject().put("data", updatedUser).toString())
                                            apply()
                                        }

                                        Toast.makeText(
                                            this@ActivityEditProfil,
                                            "Profil berhasil diperbarui",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@ActivityEditProfil,
                                            "Gagal menemukan data pengguna yang diperbarui",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e("ActivityEditProfil", "Error parsing response", e)
                                    Toast.makeText(
                                        this@ActivityEditProfil,
                                        "Gagal memproses respons dari server",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@ActivityEditProfil,
                                    "Gagal memperbarui profil: ${response.code}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            } else {
                Log.e("ActivityEditProfil", "Invalid URL")
                Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("ActivityEditProfil", "Gagal mengambil ID pengguna dari SharedPreferences")
            Toast.makeText(this, "Gagal mengambil ID pengguna", Toast.LENGTH_SHORT).show()
        }
    }

}
