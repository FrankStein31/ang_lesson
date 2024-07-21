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

class AnakAdapter(
    private val dataAnak: ArrayList<ListMurid>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AnakAdapter.HolderDataAnak>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataAnak {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anak, parent, false)
        return HolderDataAnak(view)
    }

    override fun getItemCount(): Int {
        return dataAnak.size
    }

    override fun onBindViewHolder(holder: HolderDataAnak, position: Int) {
        val currentItem = dataAnak[position]
        holder.txNama.text = currentItem.nama
        holder.txAsal.text = currentItem.asal_sekolah
        holder.txLayanan.text = currentItem.nama_layanan


        holder.itemView.setOnClickListener {
            onItemClick(currentItem.id_murid)
        }
    }

    inner class HolderDataAnak(view: View) : RecyclerView.ViewHolder(view) {
        val txNama: TextView = view.findViewById(R.id.txNama)
        val txAsal: TextView = view.findViewById(R.id.txAsal)
        val txLayanan: TextView = view.findViewById(R.id.txLayanan)
    }

    fun setData(data: List<ListMurid>) {
        dataAnak.clear()
        dataAnak.addAll(data)
        notifyDataSetChanged()
    }
}
