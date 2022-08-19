package com.project.sikasir.toko

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
class adapterToko(private val listT: ArrayList<classToko>) :
        RecyclerView.Adapter<adapterToko.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_toko, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return listT.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaT: TextView = itemView.findViewById(R.id.tv_namaT)
        val alamatT: TextView = itemView.findViewById(R.id.tv_AlamatT)
        val telpT: TextView = itemView.findViewById(R.id.tv_telpT)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val T = listT[position]

        holder.namaT.text = T.nama_Toko
        holder.alamatT.text = T.alamat
        holder.telpT.text = T.no_Telp

        holder.itemView.setOnClickListener {
            val nama_Toko = T.nama_Toko
            val alamat = T.alamat
            val no_Telp = T.no_Telp
            val id_toko = T.id_Toko
            val email = T.email

            val intent = Intent(holder.itemView.context, kelolaToko::class.java)
            intent.putExtra("nama_Toko", nama_Toko)
            intent.putExtra("alamat", alamat)
            intent.putExtra("no_Telp", no_Telp)
            intent.putExtra("id_Toko", id_toko)
            intent.putExtra("email", email)
            holder.itemView.context.startActivity(intent)
        }
    }
}