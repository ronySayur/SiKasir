package com.project.sikasir.laporan.pembelian

import android.content.Intent
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
class adapterLaporanPembelian(private val listLapPembelian: ArrayList<classLapPembelian>) : RecyclerView.Adapter<adapterLaporanPembelian.MyViewHolder>() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_laporan_pembelian, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = listLapPembelian[position]

        val nama = currentitem.nama_Vendor
        val tPembelian = Rp.format(currentitem.totalPembelian)
        val tTransaksi = currentitem.jumlahTransaksi

        holder.nama_vendor.text = nama
        holder.totalPembelian.text = tPembelian.toString()
        holder.totalTransaksi.text = tTransaksi.toString() + " Transaksi"

        holder.itemView.setOnClickListener {
            val nama_Vendor = currentitem.nama_Vendor
            val total_Pembelian = Rp.format(currentitem.totalPembelian)
            val jumlahTransaksi = currentitem.jumlahTransaksi
            val no_Vendor = currentitem.no_Vendor
            val email_Vendor = currentitem.email_Vendor
            val nama_PIC = currentitem.nama_PIC
            val No_PIC = currentitem.noPIC
            val keterangan = currentitem.keterangan

            val intent = Intent(holder.itemView.context, invoiceSup::class.java)
            intent.putExtra("nama_Vendor", nama_Vendor)
            intent.putExtra("jumlahTransaksi", jumlahTransaksi)
            intent.putExtra("total_Pembelian", total_Pembelian)
            intent.putExtra("no_Vendor", no_Vendor)
            intent.putExtra("email_Vendor", email_Vendor)
            intent.putExtra("nama_PIC", nama_PIC)
            intent.putExtra("No_PIC", No_PIC)
            intent.putExtra("keterangan", keterangan)

            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listLapPembelian.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama_vendor: TextView = itemView.findViewById(R.id.nama_vendor)
        val totalTransaksi: TextView = itemView.findViewById(R.id.list_total_transaksi)
        val totalPembelian: TextView = itemView.findViewById(R.id.tv_pembelian_total)
    }

}