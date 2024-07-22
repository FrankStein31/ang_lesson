package com.ajeng.ta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajeng.ta.databinding.ActivityEditProfilBinding
import okhttp3.*
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
    private var currentPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(b.root)

        loadProfileData()

        b.btnSimpan.setOnClickListener {
            saveChanges()
        }

        supportActionBar?.hide()
    }

    private fun loadProfileData() {
        val responseBody = sharedPreferences.getString("responseBody", null)
        if (responseBody != null) {
            try {
                val jsonResponse = JSONObject(responseBody)
                if (jsonResponse.has("data")) {
                    val data = jsonResponse.getJSONObject("data")
                    b.txUsername.setText(data.getString("username"))
                    b.txAlamat.setText(data.getString("alamat"))
                    b.txTelepon.setText(data.getString("telepon"))
                    b.txEmail.setText(data.getString("email"))
                    currentPassword = data.getString("password")
                } else {
                    Log.e("ActivityEditProfil", "Key 'data' not found in JSON")
                    Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ActivityEditProfil", "Error parsing JSON", e)
                Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("ActivityEditProfil", "responseBody is null")
            Toast.makeText(this, "Data profil tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveChanges() {
        val updatedUsername = b.txUsername.text.toString().trim()
        val updatedAlamat = b.txAlamat.text.toString().trim()
        val updatedTelepon = b.txTelepon.text.toString().trim()
        val updatedEmail = b.txEmail.text.toString().trim()

        if (updatedUsername.isEmpty() || updatedAlamat.isEmpty() || updatedTelepon.isEmpty() || updatedEmail.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val responseBody = sharedPreferences.getString("responseBody", null)
        var ID: String? = null
        var idAkses: String? = null
        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            if (jsonResponse.has("data")) {
                val data = jsonResponse.getJSONObject("data")
                ID = data.optString("ID")
                idAkses = data.optString("id_akses")
            }
        }

        if (ID != null && idAkses != null) {
            val requestBody = FormBody.Builder()
                .add("ID", ID)
                .add("id_akses", idAkses)
                .add("username", updatedUsername)
                .add("alamat", updatedAlamat)
                .add("telepon", updatedTelepon)
                .add("email", updatedEmail)
                .add("password", currentPassword ?: "") // Add this line to include the current password
                .build()

            val request = Request.Builder()
                .url("http://192.168.0.56:8081/TA_1/api/Manage_all/edit_profil")
                .post(requestBody)
                .build()

            Log.d("ActivityEditProfil", "Sending update request: ID=$ID, id_akses=$idAkses, username=$updatedUsername, alamat=$updatedAlamat, telepon=$updatedTelepon, email=$updatedEmail, password=$currentPassword")

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
                        try {
                            if (responseBody == null) {
                                throw Exception("Response body is null")
                            }

                            Log.d("ActivityEditProfil", "Response body: $responseBody")

                            if (responseBody.startsWith("<html>") || responseBody.startsWith("<div")) {
                                throw Exception("Server returned HTML instead of JSON")
                            }

                            val jsonObject = JSONObject(responseBody)

                            if (response.isSuccessful) {
                                if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                                    val updatedUserData = JSONObject().apply {
                                        put("ID", ID)
                                        put("id_akses", idAkses)
                                        put("username", updatedUsername)
                                        put("alamat", updatedAlamat)
                                        put("telepon", updatedTelepon)
                                        put("email", updatedEmail)
                                        put("password", currentPassword)
                                    }
                                    val updatedResponseBody = JSONObject().put("data", updatedUserData).toString()
                                    sharedPreferences.edit().putString("responseBody", updatedResponseBody).apply()

                                    Toast.makeText(
                                        this@ActivityEditProfil,
                                        "Profil berhasil diperbarui",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                } else {
                                    val message = jsonObject.optString("message", "Gagal memperbarui profil")
                                    throw Exception(message)
                                }
                            } else {
                                val errorBody = response.body?.string()
                                Log.e("ActivityEditProfil", "Error response: $errorBody")
                                throw Exception("Server returned error: ${response.code}")
                            }
                        } catch (e: Exception) {
                            Log.e("ActivityEditProfil", "Error processing response", e)
                            Toast.makeText(
                                this@ActivityEditProfil,
                                "Gagal memproses respons dari server: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            })
        } else {
            Log.e("ActivityEditProfil", "Gagal mengambil ID pengguna dari SharedPreferences")
            Toast.makeText(this, "Gagal mengambil ID pengguna", Toast.LENGTH_SHORT).show()
        }
    }
}