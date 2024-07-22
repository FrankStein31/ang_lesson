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

    companion object {
        private const val EDIT_PROFILE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txUsername = view.findViewById(R.id.txUsername)
        txAlamat = view.findViewById(R.id.txAlamat)
        txTelepon = view.findViewById(R.id.txTelepon)
        txEmail = view.findViewById(R.id.txEmail)
        btnEdit = view.findViewById(R.id.btnEdit)
        btnLogout = view.findViewById(R.id.btnLogout)

        loadProfileData()

        btnEdit.setOnClickListener {
            val responseBody = sharedPreferences.getString("responseBody", null)
            if (responseBody != null) {
                val intent = Intent(requireActivity(), ActivityEditProfil::class.java)
                intent.putExtra("responseBody", responseBody)
                startActivityForResult(intent, EDIT_PROFILE_REQUEST)
            } else {
                Log.e("item profile", "responseBody is null")
                Toast.makeText(requireActivity(), "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun loadProfileData() {
        val responseBody = sharedPreferences.getString("responseBody", null)
        if (responseBody != null) {
            try {
                val jsonResponse = JSONObject(responseBody)
                val data = jsonResponse.getJSONObject("data")
                txUsername.text = data.getString("username")
                txAlamat.text = data.getString("alamat")
                txTelepon.text = data.getString("telepon")
                txEmail.text = data.getString("email")

                Log.d("item profile", "Data loaded: $jsonResponse")
            } catch (e: Exception) {
                Log.e("item profile", "Error parsing JSON", e)
                Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("item profile", "responseBody is null")
            Toast.makeText(context, "Data login tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            loadProfileData()
        }
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