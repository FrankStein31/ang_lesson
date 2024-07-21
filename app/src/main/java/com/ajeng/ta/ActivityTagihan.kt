package com.ajeng.ta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajeng.ta.services.ApiService
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.snap.Gopay
import com.midtrans.sdk.corekit.models.snap.Shopeepay
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityTagihan : AppCompatActivity() , TransactionFinishedCallback {
    lateinit var adapter: TagihanAdapter
    private var responseBody: String? = null
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    }
    var id_tagihan = ""
    var nama = ""
    var bulan = ""
    var harga = ""

    var id_user = ""

    lateinit var iya: Button
    lateinit var tidak: Button
    lateinit var konfirmasi: CardView

    var id_transaksi = ""
    interface ApiService {
        @GET("tagihan_json/{id_user}")
        fun getTagihanById(@Path("id_user") id_user: String): Call<ArrayList<ListTagihan>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tagihan)
        supportActionBar?.hide()

        iya = findViewById<Button>(R.id.btn_iya)
        tidak = findViewById<Button>(R.id.btn_tidak)
        konfirmasi = findViewById<CardView>(R.id.konfirmasi)

        responseBody = intent.getStringExtra("responseBody")
        if (responseBody == null) {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.listTagihan)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TagihanAdapter(arrayListOf(), this)
        recyclerView.adapter = adapter

        if (responseBody != null) {
            val jsonResponse = JSONObject(responseBody)
            val data = jsonResponse.getJSONObject("data")
            id_user = data.getString("ID")
            Log.e("ID", "ID USER : $id_user")
            getTagihan(id_user)
        } else {
            responseBody = sharedPreferences.getString("responseBody", null)
        }

        iya.setOnClickListener{
            val timeStamp = SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault()).format(Date())
            id_transaksi = "TRANS"+timeStamp
            payment()
        }
        tidak.setOnClickListener{
            konfirmasi.visibility = View.GONE
        }
    }

    fun payment(){
        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-8xUGAWkUHyaColPV") // client_key is mandatory
            .setContext(this) // context is mandatory
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl("http:192.168.0.56:8081/TA_1/midtrans/charge/") //set merchant url
            .enableLog(true) // enable sdk log
            .setColorTheme(CustomColorTheme("#3C486B", "#3C486B", "#3C486B"))
            // will replace theme on snap theme on MAP
            .setLanguage("id")
        sdkUIFlowBuilder.buildSDK()
        MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

    private fun initTransactionRequest(): TransactionRequest {
        val transactionRequestNew = TransactionRequest(id_transaksi, harga.toDouble())
        transactionRequestNew.customerDetails = initCustomerDetails()
        transactionRequestNew.itemDetails = initItemDetail()
        transactionRequestNew.gopay = Gopay("mysamplesdk:://midtrans")
        transactionRequestNew.shopeepay = Shopeepay("mysamplesdk:://midtrans")
        return transactionRequestNew
    }

    private fun initItemDetail(): ArrayList<ItemDetails>? {
        val itemList = ArrayList<ItemDetails>()
        val harga1 = harga.toDouble()
        val nama1 = "tagihan bulan ${bulan}"
        itemList.add(ItemDetails("1", harga1, 1, nama1))
        return itemList
    }

    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> {
                    updateTagihan()
                    Toast.makeText(
                        this,
                        "Transaction Finished",
                        Toast.LENGTH_LONG
                    ).show()


                }

                TransactionResult.STATUS_PENDING ->{
                    Toast.makeText(
                        this,
                        "Transaction Pending" ,
                        Toast.LENGTH_LONG
                    ).show()

                }

                TransactionResult.STATUS_FAILED ->{

                    Toast.makeText(
                        this,
                        "Transaction Failed. Message: " + result.response.statusMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
//            result.response.getValidationMessages()
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()

        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, ignoreCase = true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun initCustomerDetails(): CustomerDetails {
        //define customer detail (mandatory for coreflow)
        val mCustomerDetails = CustomerDetails()
        mCustomerDetails.phone = "03342423"
        mCustomerDetails.firstName = "ddddd"
        mCustomerDetails.email = "sss@ddd.cc"
        mCustomerDetails.customerIdentifier = "sss@ddd.cc"
        return mCustomerDetails
    }
    private fun getTagihan(id_user: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http:192.168.0.56:8081/TA_1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val call = apiService.getTagihanById(id_user)

        call.enqueue(object : Callback<ArrayList<ListTagihan>> {
            override fun onResponse(call: Call<ArrayList<ListTagihan>>, response: Response<ArrayList<ListTagihan>>) {
                if (response.isSuccessful) {
                    showNoTagihanMessage(false)
                    val TagihanList = response.body()
                    if (TagihanList != null && TagihanList.isNotEmpty()) {
                        adapter.setData(TagihanList)
                    } else {
                        // Handle case when rekapList is empty
                        showNoTagihanMessage(true)
                        Log.e("Dashboard error sukses", "Gagal mendapatkan data tagihan: ${response.message()}")
                    }
                } else {
                    showNoTagihanMessage(true)
                    Log.e("Dashboard", "Gagal mendapatkan data tagihan: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<ListTagihan>>, t: Throwable) {
                showNoTagihanMessage(true)
                Log.e("Dashboard", "API gagal")
                t.printStackTrace()
            }
        })
    }

    private fun updateTagihan() {
        val url = "http:192.168.0.56:8081/TA_1/api/Manage_all/edit_tagihan/${id_tagihan}"
        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val success = jsonObject.getString("success")
                    if (success == "true") {
                        getTagihan(id_user)
                    } else {
                        val message = jsonObject.getString("message")
                        showUpdateFailedMessage(message)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showUpdateFailedMessage("Terjadi kesalahan saat memproses respons.")
                }
            },
            { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
                error.printStackTrace()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_tagihan"] = id_tagihan
                return params
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showUpdateFailedMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showNoTagihanMessage(show: Boolean) {
        val recyclerViewTagihan = findViewById<RecyclerView>(R.id.listTagihan)
        recyclerViewTagihan.visibility = if (show) View.GONE else View.VISIBLE
    }
}