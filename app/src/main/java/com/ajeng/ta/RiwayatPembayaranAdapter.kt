package com.ajeng.ta

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiwayatPembayaranAdapter (private val dataRiwayatPembayaran: ArrayList<ListRiwayatPembayaran>, private val context: Context) :
    RecyclerView.Adapter<RiwayatPembayaranAdapter.HolderDataRiwayatPembayaran>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataRiwayatPembayaran {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat_pembayaran, parent, false)
        return HolderDataRiwayatPembayaran(view)
    }

    override fun getItemCount(): Int {
        return dataRiwayatPembayaran.size
    }

    override fun onBindViewHolder(holder: HolderDataRiwayatPembayaran, position: Int) {
        val currentItem = dataRiwayatPembayaran[position]
        holder.txNama.text = currentItem.nama
        holder.txBulan.text = currentItem.bulan
        holder.txJumlah.text = currentItem.jumlah
        holder.txTanggal.text = currentItem.tanggal
        holder.txStatus.text = currentItem.status_tagihan

        // Uncomment dan sesuaikan jika Anda menggunakan Picasso atau pustaka serupa untuk memuat gambar
        // if (!data["url"].isNullOrEmpty()) {
        //    Picasso.get().load(data["url"]).into(holder.photo)
        // }
    }

    inner class HolderDataRiwayatPembayaran(view: View) : RecyclerView.ViewHolder(view) {
        val txNama: TextView = view.findViewById(R.id.txNama)
        val txBulan: TextView = view.findViewById(R.id.txBulan)
        val txJumlah: TextView = view.findViewById(R.id.txJumlah)
        val txTanggal: TextView = view.findViewById(R.id.txTanggal)
        val txStatus: TextView = view.findViewById(R.id.txStatus)
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

    fun setData(data: List<ListRiwayatPembayaran>) {
        dataRiwayatPembayaran.clear()
        dataRiwayatPembayaran.addAll(data)
        notifyDataSetChanged()
    }
}


