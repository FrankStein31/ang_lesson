package com.ajeng.ta

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class JadwalAdapter (private val dataJadwal: ArrayList<ListJadwal>, private val context: Context) :
        RecyclerView.Adapter<JadwalAdapter.HolderDataJadwal>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataJadwal {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal, parent, false)
            return HolderDataJadwal(view)
        }

        override fun getItemCount(): Int {
            return dataJadwal.size
        }

        override fun onBindViewHolder(holder: HolderDataJadwal, position: Int) {
            val currentItem = dataJadwal[position]
            holder.txLayan.text = currentItem.nama_layanan
            holder.txHari.text = currentItem.hari
            holder.txMulai.text = currentItem.jam_mulai
            holder.txBerakhir.text = currentItem.jam_berakhir

            // Uncomment dan sesuaikan jika Anda menggunakan Picasso atau pustaka serupa untuk memuat gambar
            // if (!data["url"].isNullOrEmpty()) {
            //    Picasso.get().load(data["url"]).into(holder.photo)
            // }
        }

        inner class HolderDataJadwal(view: View) : RecyclerView.ViewHolder(view) {
            val txLayan: TextView = view.findViewById(R.id.txLayan)
            val txHari: TextView = view.findViewById(R.id.txHari)
            val txMulai: TextView = view.findViewById(R.id.txMulai)
            val txBerakhir: TextView = view.findViewById(R.id.txBerakhir)
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

        fun setData(data: List<ListJadwal>) {
            dataJadwal.clear()
            dataJadwal.addAll(data)
            notifyDataSetChanged()
        }
    }


