package com.ajeng.ta

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TagihanAdapter (private val dataTagihan: ArrayList<ListTagihan>, val mainActivity: ActivityTagihan):
    RecyclerView.Adapter<TagihanAdapter.HolderDataTagihan>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataTagihan {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tagihan, parent, false)
        return HolderDataTagihan(view)
    }

    override fun getItemCount(): Int {
        return dataTagihan.size
    }

    override fun onBindViewHolder(holder: HolderDataTagihan, position: Int) {
        val currentItem = dataTagihan[position]
        holder.txNama.text = currentItem.nama
        holder.txBulan.text = currentItem.bulan
        holder.txJumlah.text = currentItem.jumlah
        holder.txStatus.text = currentItem.status_tagihan

        holder.cardView.setOnClickListener {
            mainActivity.konfirmasi.visibility = View.VISIBLE
            mainActivity.id_tagihan = currentItem.id_tagihan
            mainActivity.nama = currentItem.nama
            mainActivity.bulan = currentItem.bulan
            mainActivity.harga = currentItem.jumlah
        }
    }

    inner class HolderDataTagihan(view: View) : RecyclerView.ViewHolder(view) {
        val txNama: TextView = view.findViewById(R.id.txNama)
        val txBulan: TextView = view.findViewById(R.id.txBulan)
        val txJumlah: TextView = view.findViewById(R.id.txJumlah)
        val txStatus: TextView = view.findViewById(R.id.txStatus)
        val cardView: CardView = view.findViewById(R.id.cardList)
    }

    fun setData(data: List<ListTagihan>) {
        dataTagihan.clear()
        dataTagihan.addAll(data)
        notifyDataSetChanged()
    }
}