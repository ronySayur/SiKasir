package com.project.sikasir.penjualan.riwayat

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.produk.produk.classProduk
import kotlinx.android.synthetic.main.activity_detail_riwayat.*

class detailRiwayat : AppCompatActivity() {
    val riwayatList = ArrayList<classProduk>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_riwayat)
        getRiwayat()
    }

    private fun getRiwayat() {
        rv_riw.layoutManager = GridLayoutManager(this, 1)
        rv_riw.setHasFixedSize(true)

        val refProduk = FirebaseDatabase.getInstance().getReference("Penjualan")

        refProduk.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rv_riw.visibility = View.VISIBLE
                    riwayatList.clear()
                    for (snapProd in snapshot.children) {
                        val kat = snapProd.getValue(classProduk::class.java)
                        val stok = snapProd.child("namaPegawai")

                    }
                    /*       rv_riw.adapter = adapterTransaksi(riwayatList, trasaksiListener, produkListener)*/
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}