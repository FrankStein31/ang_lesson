package com.ajeng.ta

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
import androidx.fragment.app.FragmentTransaction
import org.json.JSONObject

class FragmentProfil : Fragment() {
    lateinit var txUsername: TextView
    lateinit var txAlamat: TextView
    lateinit var txTelepon: TextView
    lateinit var txEmail: TextView
    lateinit var btnEdit: Button
    lateinit var btnLogout: Button
    lateinit var ft: FragmentTransaction
    private val sharedPreferences: SharedPreferences by lazy {
        requireActivity().getSharedPreferences("login_prefs", AppCompatActivity.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txUsername = view.findViewById(R.id.txUsername)
        txAlamat = view.findViewById(R.id.txAlamat)
        txTelepon = view.findViewById(R.id.txTelepon)
        txEmail = view.findViewById(R.id.txEmail)
        btnEdit = view.findViewById(R.id.btnEdit)
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

        btnEdit.setOnClickListener {
            var responseBody = requireActivity().intent.getStringExtra("responseBody")
            if (responseBody == null) {
                responseBody = sharedPreferences.getString("responseBody", null)
            }

            if (responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                Log.e("item profile", "Data : $jsonResponse")

                // Intent untuk mengirim responseBody ke ActivityEditProfil
                val intent = Intent(requireActivity(), ActivityEditProfil::class.java)
                intent.putExtra("responseBody", responseBody)
                startActivity(intent)
            } else {
                Log.e("item profile", "responseBody is null")
                // Tindakan jika responseBody null, misalnya tampilkan pesan kesalahan
                Toast.makeText(requireActivity(), "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
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
