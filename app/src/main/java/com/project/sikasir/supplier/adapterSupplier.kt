package com.project.sikasir.supplier

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.sikasir.R

/**
 * Dibuat oleh RizkyPratama pada 08-May-22.
 */
class adapterSupplier(private val listSup: ArrayList<classSupplier>) :
        RecyclerView.Adapter<adapterSupplier.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_supplier, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return listSup.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_namavendor: TextView = itemView.findViewById(R.id.tv_namavendor)
        val tv_pic: TextView = itemView.findViewById(R.id.tv_pic)
        val tv_hp_sup: TextView = itemView.findViewById(R.id.tv_hp_sup)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = listSup[position]

        holder.tv_namavendor.text = currentitem.nama_Vendor
        holder.tv_pic.text = currentitem.nama_PIC
        holder.tv_hp_sup.text = currentitem.no_Vendor


        holder.itemView.setOnClickListener {
            val nama_Vendor = listSup[position].nama_Vendor
            val alamat_Vendor = listSup[position].alamat_Vendor
            val email_Vendor = listSup[position].email_Vendor
            val no_Vendor = listSup[position].no_Vendor
            val nama_PIC = listSup[position].nama_PIC
            val jabatan_PIC = listSup[position].jabatan_PIC
            val No_PIC = listSup[position].noPIC

            val intent = Intent(holder.itemView.context, kelolaSupplier::class.java)
            intent.putExtra("nama_Vendor", nama_Vendor)
            intent.putExtra("alamat_Vendor", alamat_Vendor)
            intent.putExtra("email_Vendor", email_Vendor)
            intent.putExtra("no_Vendor", no_Vendor)
            intent.putExtra("nama_PIC", nama_PIC)
            intent.putExtra("jabatan_PIC", jabatan_PIC)
            intent.putExtra("noPIC", No_PIC)

            holder.itemView.context.startActivity(intent)
        }
    }
}