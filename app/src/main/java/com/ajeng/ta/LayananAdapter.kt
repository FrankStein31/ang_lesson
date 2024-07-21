package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

//class LayananAdapter(private val dataLayanan: ArrayList<ListLayanan>, val mainActivity: ActivityLayananTentor, private val onItemClick: (String) -> Unit) :
//    RecyclerView.Adapter<LayananAdapter.HolderDataLayanan>() {
class LayananAdapter(
    private val dataLayanan: ArrayList<ListLayanan>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<LayananAdapter.HolderDataLayanan>() {

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
//
//            mainActivity.nama_layanan = currentItem.nama_layanan
//            mainActivity.keterangan = currentItem.keterangan
//            mainActivity.nama = currentItem.nama
//            mainActivity.biaya = currentItem.biaya
//        }

        holder.itemView.setOnClickListener {
            onItemClick(currentItem.id_layanan)
        }
    }

    inner class HolderDataLayanan(view: View) : RecyclerView.ViewHolder(view) {
        val txLayanan: TextView = view.findViewById(R.id.txLayanan)
        val txKet: TextView = view.findViewById(R.id.txKet)
        val txTentor: TextView = view.findViewById(R.id.txTentor)
        val txHarga: TextView = view.findViewById(R.id.txHarga)
        val cardView: CardView = view.findViewById(R.id.cardList)
    }

    fun setData(data: List<ListLayanan>) {
        dataLayanan.clear()
        dataLayanan.addAll(data)
        notifyDataSetChanged()
    }
}
