package com.ajeng.ta

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class FragmentProfilTentor : Fragment() {
    lateinit var txUsername: TextView
    lateinit var txAlamat: TextView
    lateinit var txTelepon: TextView
    lateinit var txEmail: TextView
    lateinit var btnLogout: Button
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("login_prefs", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txUsername = view.findViewById(R.id.txUsername)
        txAlamat = view.findViewById(R.id.txAlamat)
        txTelepon = view.findViewById(R.id.txTelepon)
        txEmail = view.findViewById(R.id.txEmail)
        btnLogout = view.findViewById(R.id.btnLogout)

        val responseBody = arguments?.getString("responseBody") ?: sharedPreferences.getString("responseBody", null)
        if (responseBody != null) {
            try {
                val jsonResponse = JSONObject(responseBody)
                val data = jsonResponse.getJSONObject("data")
                val username = data.getString("username")
                val alamat = data.getString("alamat")
                val telepon = data.getString("telepon")
                val email = data.getString("email")

                txUsername.text = username
                txAlamat.text = alamat
                txTelepon.text = telepon
                txEmail.text = email

                Log.e("item profile", "Data: $jsonResponse")
            } catch (e: Exception) {
                Log.e("item profile", "Error parsing JSON", e)
                Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("item profile", "responseBody is null")
            Toast.makeText(context, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        with(sharedPreferences.edit()) {
            clear()
            putBoolean("isLoggedIn", false)
            apply()
        }

        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
