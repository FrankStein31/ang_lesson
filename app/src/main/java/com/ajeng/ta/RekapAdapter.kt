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
import com.ajeng.ta.databinding.ActivityHomeTentorBinding
import com.bumptech.glide.Glide

class RekapAdapter(private val dataRekap: ArrayList<ListRekap>, private val context: Context) :
    RecyclerView.Adapter<RekapAdapter.HolderDataRekap>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataRekap {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rekap, parent, false)
        return HolderDataRekap(view)
    }

    override fun getItemCount(): Int {
        return dataRekap.size
    }

    override fun onBindViewHolder(holder: HolderDataRekap, position: Int) {
        val currentItem = dataRekap[position]
        holder.txTanggal.text = currentItem.tanggal
        holder.txJam.text = currentItem.jam
        holder.txMateri.text = currentItem.materi
        holder.txStatus.text = currentItem.status

//        Glide.with(holder.itemView.context)
//            .load("http:192.168.0.5/TA_1/assets/file/${currentItem.bukti}")
//            .into(holder.bukti)
        // Uncomment dan sesuaikan jika Anda menggunakan Picasso atau pustaka serupa untuk memuat gambar
        // if (!data["url"].isNullOrEmpty()) {
        //    Picasso.get().load(data["url"]).into(holder.photo)
        // }
    }

    inner class HolderDataRekap(view: View) : RecyclerView.ViewHolder(view) {
        val txTanggal: TextView = view.findViewById(R.id.txTanggal)
        val txJam: TextView = view.findViewById(R.id.txJam)
        val txMateri: TextView = view.findViewById(R.id.txMateri)
        val txStatus: TextView = view.findViewById(R.id.txStatus)
//        val bukti: ImageView = view.findViewById(R.id.imgBukti )
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

    fun setData(data: List<ListRekap>) {
        dataRekap.clear()
        dataRekap.addAll(data)
        notifyDataSetChanged()
    }
}