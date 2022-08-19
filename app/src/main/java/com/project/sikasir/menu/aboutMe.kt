package com.project.sikasir.menu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.produk.produk.classProduk
import kotlinx.android.synthetic.main.about.*
import java.util.*

class aboutMe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about)

        tv_nama.setOnClickListener {
            val refTransaksi = FirebaseDatabase.getInstance().getReference("Penjualan").orderByValue()
            refTransaksi.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshotPegawai: DataSnapshot) {
                    if (snapshotPegawai.exists()) {
                        println(snapshotPegawai)
                        val t = snapshotPegawai.getValue(classProduk::class.java)
                        println(t)
                        println(UUID.randomUUID().toString())
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
