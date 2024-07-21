package com.ajeng.ta

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okio.IOException

class RegisterActivity : AppCompatActivity() {
    private var iv_show_hide_pwd = false
    private lateinit var edUsername: EditText
    private lateinit var edAlamat: EditText
    private lateinit var edTelepon: EditText
    private lateinit var edEmail: EditText
    private lateinit var txLogin: TextView
    private lateinit var edPassword: EditText
    private lateinit var eye: ImageView
    private lateinit var logoregis: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        val btnRegis: Button = findViewById(R.id.btnRegis)
        edUsername = findViewById(R.id.edUsername)
        edAlamat = findViewById(R.id.edAlamat)
        edTelepon = findViewById(R.id.edTelepon)
        edEmail = findViewById(R.id.edEmail)
        edPassword = findViewById(R.id.edPassword)
        txLogin = findViewById(R.id.txLogin)
        eye = findViewById(R.id.iv_show_hide_pwd)
        logoregis = findViewById(R.id.logoregis)

        // Set gambar default untuk ivShowHidePwd
        eye.setImageResource(R.drawable.show_eye)
        logoregis.setImageResource(R.drawable.logo_baru)

        txLogin.setOnClickListener {
            // Navigasi ke layar login (Buka komentar jika diperlukan)
             val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
             startActivity(intent)
        }

        btnRegis.setOnClickListener {
            val username = edUsername.text.toString().trim()
            val alamat = edAlamat.text.toString().trim()
            val telepon = edTelepon.text.toString().trim()
            val email = edEmail.text.toString().trim()
            val password = edPassword.text.toString().trim()

            if (username.isEmpty() || alamat.isEmpty() || telepon.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Mohon isi semua kolom.", Toast.LENGTH_SHORT).show()
            } else {
                // Log nilai sebelum mengirim permintaan
                Log.d("RegisterActivity", "Username: $username")
                Log.d("RegisterActivity", "Alamat: $alamat")
                Log.d("RegisterActivity", "Telepon: $telepon")
                Log.d("RegisterActivity", "Email: $email")
                Log.d("RegisterActivity", "Password: $password")

                val registerRequest = RegisterRequest(username, alamat, email, telepon, password)
                register(registerRequest)
            }
        }

        // Fungsi show dan hide password
        val ivShowHidePwd: ImageView = findViewById(R.id.iv_show_hide_pwd)
        ivShowHidePwd.setOnClickListener {
            iv_show_hide_pwd = !iv_show_hide_pwd
            if (iv_show_hide_pwd) {
                // Jika password sebelumnya disembunyikan, tampilkan sebagai teks biasa
                edPassword.transformationMethod = null
                ivShowHidePwd.setImageResource(R.drawable.close_eye)
            } else {
                // Jika password sebelumnya ditampilkan, sembunyikan dengan karakter bintang
                edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivShowHidePwd.setImageResource(R.drawable.show_eye)
            }
            // Set kursor ke akhir teks
            edPassword.setSelection(edPassword.text.length)
        }
    }

    private fun register(registerRequest: RegisterRequest) {
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("username", registerRequest.username)
            .add("alamat", registerRequest.alamat)
            .add("telepon", registerRequest.telepon)
            .add("email", registerRequest.email)
            .add("password", registerRequest.password)
            .build()

        val request = Request.Builder()
            .url("http:192.168.0.56:8081/TA_1/api/Manage_all/register")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal. Terjadi kesalahan jaringan. ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, responseBody ?: "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        // Uncomment this line if you want to navigate to MainActivity after successful registration
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                } else {
                    val errorMessage = response.body?.string() ?: "Unknown error"
                    val statusCode = response.code
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registrasi gagal. $statusCode - $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

data class RegisterRequest(
    val username: String,
    val alamat: String,
    val email: String,
    val telepon: String,
    val password: String
)

data class RegisterResponse(
    val message: String
)
