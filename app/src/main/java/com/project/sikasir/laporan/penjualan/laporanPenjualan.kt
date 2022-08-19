package com.project.sikasir.laporan.penjualan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.penjualan.pembayaran.classPenjualan
import kotlinx.android.synthetic.main.laporan_penjualan.*

class laporanPenjualan : AppCompatActivity() {
    val penjualan = ArrayList<classPenjualan>()

    private lateinit var ssTransaksi: DataSnapshot

    private var awal = -1L
    private var akhir = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laporan_penjualan)
        getData()
        tvA3toA6.setOnClickListener { finish() }
        editTextDate4.setOnClickListener { tgl() }
    }

    private fun tgl() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(supportFragmentManager, "DatePicker")

        editTextDate4.isEnabled = false
        Toast.makeText(this, "tunggu sebentar", Toast.LENGTH_SHORT).show()

        datePicker.addOnPositiveButtonClickListener {
            awal = datePicker.selection!!.first
            akhir = datePicker.selection!!.second

            editTextDate4.setText(datePicker.headerText)
            editTextDate4.isEnabled = true
            initAdapter()
        }

        datePicker.addOnNegativeButtonClickListener { editTextDate4.isEnabled = true }

        datePicker.addOnCancelListener { editTextDate4.isEnabled = true }
    }

    private fun getData() {
        rv_riwayat.layoutManager = GridLayoutManager(this, 1)
        rv_riwayat.setHasFixedSize(true)

        val refTransaksi = FirebaseDatabase.getInstance().getReference("Penjualan")

        refTransaksi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotPembelian: DataSnapshot) {
                if (snapshotPembelian.exists()) {
                    ssTransaksi = snapshotPembelian
                    initAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initAdapter() {
        if (this::ssTransaksi.isInitialized) {
            penjualan.clear()
            if (awal == -1L && akhir == -1L) {
                for (snapPenjualan in ssTransaksi.children) {
                    val cd = snapPenjualan.getValue(classPenjualan::class.java)
                    penjualan.add(cd!!)
                }
            } else {
                for (snapPenjualan in ssTransaksi.children) {
                    val cd = snapPenjualan.getValue(classPenjualan::class.java)
                    if (cd?.tanggal!! >= awal && cd.tanggal!! <= akhir) {
                        val cd = snapPenjualan.getValue(classPenjualan::class.java)
                        penjualan.add(cd!!)
                    }
                }
            }
            rv_riwayat.adapter = adapterLaporanPenjualan(penjualan)
        }
    }
}