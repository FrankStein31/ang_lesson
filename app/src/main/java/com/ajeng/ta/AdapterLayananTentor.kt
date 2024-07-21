package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
//import com.ajeng.ta.databinding.ActivityHomeBinding

class AdapterLayananTentor(private val dataLayanan: ArrayList<ListLayanan>, val mainActivity: ActivityLayanan) :
    RecyclerView.Adapter<AdapterLayananTentor.HolderDataLayanan>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataLayanan {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layanan, parent, false)
        return HolderDataLayanan(view)
    }

    override fun getItemCount(): Int {
        return dataLayanan.size
    }

    override fun onBindViewHolder(holder: HolderDataLayanan, position: Int) {
        val currentItem = dataLayanan[position]
        holder.txLayanan.text = currentItem.nama_layanan
        holder.txKet.text = currentItem.keterangan
        holder.txTentor.text = currentItem.nama
        holder.txHarga.text = currentItem.biaya



//        holder.cardView.setOnClickListener {
//            mainActivity.konfirmasi.visibility = View.VISIBLE
//            mainActivity.nama_layanan = currentItem.nama_layanan
//            mainActivity.keterangan = currentItem.keterangan
//            mainActivity.nama = currentItem.nama
//            mainActivity.biaya = currentItem.biaya
//        }

        // Uncomment dan sesuaikan jika Anda menggunakan Picasso atau pustaka serupa untuk memuat gambar
        // if (!data["url"].isNullOrEmpty()) {
        //    Picasso.get().load(data["url"]).into(holder.photo)
        // }
    }

    inner class HolderDataLayanan(view: View) : RecyclerView.ViewHolder(view) {
        val txLayanan: TextView = view.findViewById(R.id.txLayanan)
        val txKet: TextView = view.findViewById(R.id.txKet)
        val txTentor: TextView = view.findViewById(R.id.txTentor)
        val txHarga: TextView = view.findViewById(R.id.txHarga)
//        val cardView: CardView = view.findViewById(R.id.cardList)
        // val photo: ImageView = view.findViewById(R.id.photo) // Uncomment jika menggunakan ImageView
//        val btn: Button = itemView.findViewById(R.id.btnJadwal)
//
//        init {
//            // Set click listener for button2
//            btn.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    val currentItem = ListLayanan[position]
//                    val intent = Intent(context, Jadwal_Layanan::class.java)
//                    intent.putExtra("id", currentItem.id)
//                    Log.d("id", "ID saya adalah: ${currentItem.id}")
//                    context.startActivity(intent)
//                }
//            }
    }

    fun setData(data: List<ListLayanan>) {
        dataLayanan.clear()
        dataLayanan.addAll(data)
        notifyDataSetChanged()
    }
}

