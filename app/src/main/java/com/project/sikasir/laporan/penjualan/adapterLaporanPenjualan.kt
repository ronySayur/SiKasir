package com.project.sikasir.laporan.penjualan

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R
import com.project.sikasir.penjualan.pembayaran.classPenjualan
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterLaporanPenjualan(private val listPenjualan: ArrayList<classPenjualan>) :
        RecyclerView.Adapter<adapterLaporanPenjualan.MyViewHolder>() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_laporan_penjualan, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listPenjualan[position]

        val tgl = currentitem.tanggal?.toTimeDateString()
        val nota = currentitem.tanggal
        val total = currentitem.total

        holder.tanggal.text = tgl
        holder.no_nota.text = nota.toString()
        holder.total.text = total

        holder.itemView.setOnClickListener {
            val namaPegawai = listPenjualan[position].namaPegawai
            val tanggal = listPenjualan[position].tanggal?.toTimeDateString()
            val nt = listPenjualan[position].tanggal.toString()
            val diskon = listPenjualan[position].diskon
            val jabatan = listPenjualan[position].jabatan
            val total_Modal = listPenjualan[position].total_Modal
            val detailTransaksi = listPenjualan[position].detailTransaksi
            val total = listPenjualan[position].total
            val emailPegawai = listPenjualan[position].emailPegawai

            val intent = Intent(holder.itemView.context, invoice::class.java)
            intent.putExtra("namaPegawai", namaPegawai)
            intent.putExtra("tanggal", tanggal)
            intent.putExtra("nt", nt)
            intent.putExtra("diskon", diskon)
            intent.putExtra("jabatan", jabatan)
            intent.putExtra("total_Modal", total_Modal)
            intent.putExtra("detailTransaksi", detailTransaksi)
            intent.putExtra("total", total)
            intent.putExtra("emailPegawai", emailPegawai)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listPenjualan.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggal: TextView = itemView.findViewById(R.id.list_penjualan_tgl)
        val no_nota: TextView = itemView.findViewById(R.id.list_nonoata)
        val total: TextView = itemView.findViewById(R.id.list_total_penjualan)
    }

    fun Long.toTimeDateString(): String {
        val dateTime = Date(this)
        val format = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        return format.format(dateTime)
    }

}