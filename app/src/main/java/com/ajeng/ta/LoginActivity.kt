package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var logologin: ImageView
    private var iv_show_hide_pwd = false
    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var eye: ImageView
    private lateinit var btnLogin: Button
    private lateinit var txSignUp: TextView
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        edUsername = findViewById(R.id.edUsername)
        edPassword = findViewById(R.id.edPassword)
        eye = findViewById(R.id.iv_show_hide_pwd)
        btnLogin = findViewById(R.id.btnLogin)
        txSignUp = findViewById(R.id.txRegis)
        logologin = findViewById(R.id.logologin)

        txSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        eye.setImageResource(R.drawable.show_eye)
        logologin.setImageResource(R.drawable.logo_baru)

        eye.setOnClickListener {
            iv_show_hide_pwd = !iv_show_hide_pwd
            if (iv_show_hide_pwd) {
                edPassword.transformationMethod = null
                eye.setImageResource(R.drawable.close_eye)
            } else {
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                eye.setImageResource(R.drawable.show_eye)
            }
            edPassword.setSelection(edPassword.text.length)
        }

        btnLogin.setOnClickListener {
            val username = edUsername.text.toString().trim()
            val password = edPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Mohon isi username dan password", Toast.LENGTH_SHORT).show()
            } else {
                login(username, password)
            }
        }
    }

    private fun saveLoginStatus(responseBody: String) {
        try {
            val jsonObject = JSONObject(responseBody)
            val data = jsonObject.getJSONObject("data")
            val idAkses = data.optString("id_akses")
            Log.d("LoginActivity", "id_akses: $idAkses") // Log untuk memeriksa id_akses

            with(sharedPreferences.edit()) {
                putBoolean("isLoggedIn", true)
                putString("responseBody", responseBody)
                putString("id_akses", idAkses)
                apply()
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error parsing JSON: ${e.message}")
        }
    }

    private fun login(username: String, password: String) {
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("http:192.168.0.56:8081/TA_1/api/Manage_all/login")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login gagal. Terjadi kesalahan jaringan. ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        saveLoginStatus(responseBody)
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                            Log.e("Login Activity", responseBody)
                            try {
                                val jsonObject = JSONObject(responseBody)
                                val data = jsonObject.getJSONObject("data")
                                val idAkses = data.optString("id_akses")
                                Log.d("LoginActivity", "onResponse id_akses: $idAkses") // Log untuk memeriksa id_akses di onResponse
                                val intent = when (idAkses) {
                                    "A1" -> Intent(this@LoginActivity, HomeActivity::class.java)
                                    "A2" -> Intent(this@LoginActivity, HomeTentorActivity::class.java)
                                    else -> Intent(this@LoginActivity, LoginActivity::class.java) // Default activity
                                }
                                startActivity(intent)
                                finish()
                            } catch (e: Exception) {
                                Log.e("LoginActivity", "Error processing response: ${e.message}")
                            }
                        }
                    }
                } else {
                    val errorMessage = response.body?.string() ?: "Unknown error"
                    val statusCode = response.code
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login gagal. $statusCode - $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
