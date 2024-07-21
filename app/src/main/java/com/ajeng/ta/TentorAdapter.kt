package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ajeng.ta.databinding.ActivityMainBinding
import com.bumptech.glide.Glide

class TentorAdapter (private val dataTentor: ArrayList<ListTentor>, private val context: Context) :
    RecyclerView.Adapter<TentorAdapter.HolderDataTentor>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataTentor {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tentor, parent, false)
        return HolderDataTentor(view)
    }

    override fun getItemCount(): Int {
        return dataTentor.size
    }

    override fun onBindViewHolder(holder: HolderDataTentor, position: Int) {
        val currentItem = dataTentor[position]
        holder.txNamaTentor.text = currentItem.nama
        holder.txJenjang.text = currentItem.jenjang

        Glide.with(holder.itemView.context)
            .load("http:192.168.0.56:8081/TA_1/assets/file/${currentItem.foto}")
            .into(holder.foto)

        // Uncomment dan sesuaikan jika Anda menggunakan Picasso atau pustaka serupa untuk memuat gambar
//             if (!data["url"].isNullOrEmpty()) {
//                Picasso.get().load(data["url"]).into(holder.photo)
//             }
    }

    inner class HolderDataTentor(view: View) : RecyclerView.ViewHolder(view) {
        val txNamaTentor: TextView = view.findViewById(R.id.txNamaTentor)
        val txJenjang: TextView = view.findViewById(R.id.txJenjang)
        val foto: ImageView = view.findViewById(R.id.imageView ) // Uncomment jika menggunakan ImageView
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

    fun setData(data: List<ListTentor>) {
        dataTentor.clear()
        dataTentor.addAll(data)
        notifyDataSetChanged()
    }
}


