package com.project.sikasir.laporan.pegawai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import java.text.NumberFormat
import java.util.*

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterLaporanPegawai(private val listPegawai: ArrayList<classLapPegawai>) : RecyclerView.Adapter<adapterLaporanPegawai.MyViewHolder>() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_pegawai_laporan, parent, false)

        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return listPegawai.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Nama: TextView = itemView.findViewById(R.id.tv_s_nama)
        val Jabatan: TextView = itemView.findViewById(R.id.tv_s_jabatan)
        val Omset: TextView = itemView.findViewById(R.id.tv_s_total)
        val Jumlah_Transaksi: TextView = itemView.findViewById(R.id.tv_s_jml)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = listPegawai[position]

        holder.Nama.text = currentitem.namaPegawai
        holder.Jabatan.text = currentitem.jabatan
        holder.Omset.text = Rp.format(currentitem.omset)
        holder.Jumlah_Transaksi.text = currentitem.jumlahTransaksi.toString() + " Transaksi"

    }
}