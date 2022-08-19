package com.project.sikasir.laporan.rangkuman

import android.content.Intent
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
import com.project.sikasir.laporan.laporan
import com.project.sikasir.pembelian.classPembelian
import com.project.sikasir.penjualan.pembayaran.classPenjualan
import com.project.sikasir.penjualan.riwayat.adapterRiwayat
import com.project.sikasir.penjualan.riwayat.classRiwayat
import com.project.sikasir.penjualan.riwayat.riwayatTransaksi
import kotlinx.android.synthetic.main.laporan_rangkuman.*
import java.text.NumberFormat
import java.util.*

class laporanRangkuman : AppCompatActivity() {
    val cRiwayat = ArrayList<classRiwayat>()
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    private lateinit var ssPenjualan: DataSnapshot
    private lateinit var ssPembelian: DataSnapshot

    private var awal = -1L
    private var akhir = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laporan_rangkuman)
        tvA3toA3.setOnClickListener { startActivity(Intent(this, laporan::class.java)) }
        tvA3toA3.setOnClickListener { finish() }
        transaksi_detail.setOnClickListener { startActivity(Intent(this, riwayatTransaksi::class.java)) }
        editTextDate.setOnClickListener { tgl() }
        getData()
    }

    private fun tgl() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(supportFragmentManager, "DatePicker")

        editTextDate.isEnabled = false
        Toast.makeText(this, "tunggu sebentar", Toast.LENGTH_SHORT).show()

        datePicker.addOnPositiveButtonClickListener {
            awal = datePicker.selection!!.first
            akhir = datePicker.selection!!.second

            editTextDate.setText(datePicker.headerText)
            editTextDate.isEnabled = true
            initAdapter()
        }

        datePicker.addOnNegativeButtonClickListener {
            editTextDate.isEnabled = true
        }

        datePicker.addOnCancelListener {
            editTextDate.isEnabled = true
        }
    }

    private fun getData() {
        rv_riwayat.layoutManager = GridLayoutManager(this, 1)
        rv_riwayat.setHasFixedSize(true)

        val refPenjualan = FirebaseDatabase.getInstance().getReference("Penjualan").orderByValue()
        val refPembelian = FirebaseDatabase.getInstance().getReference("Pembelian")

        refPembelian.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotPembelian: DataSnapshot) {
                if (snapshotPembelian.exists()) {
                    refPenjualan.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshotPenjualan: DataSnapshot) {
                            if (snapshotPenjualan.exists()) {
                                ssPembelian = snapshotPembelian
                                ssPenjualan = snapshotPenjualan
                                initAdapter()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun initAdapter() {
        if (this::ssPenjualan.isInitialized && this::ssPembelian.isInitialized) {
            var JTPenjualan = 0
            var JTPPembelian = 0
            var TTPenjualan = 0
            var TTPembelian = 0
            cRiwayat.clear()

            if (awal == -1L && akhir == -1L) {
                for (snapBeli in ssPembelian.children) {
                    val t = snapBeli.getValue(classPembelian::class.java)
                    JTPPembelian += 1
                    TTPembelian += t?.totalPembelian?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                }
                for (snapTransaksi in ssPenjualan.children) {
                    val t = snapTransaksi.getValue(classPenjualan::class.java)
                    JTPenjualan += 1
                    TTPenjualan += t?.total?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                }
            } else {
                for (snapTransaksi in ssPenjualan.children) {
                    val t = snapTransaksi.getValue(classPenjualan::class.java)
                    if (t?.tanggal!! >= awal && t.tanggal!! <= akhir) {
                        JTPenjualan += 1
                        TTPenjualan += t.total?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                    }
                }
                for (snapBeli in ssPembelian.children) {
                    val t = snapBeli.getValue(classPembelian::class.java)
                    if (t?.tglLong!! >= awal && t.tglLong!! <= akhir) {
                        JTPPembelian += 1
                        TTPembelian += t.totalPembelian?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                    }
                }
            }

            val TPenjualan = Rp.format(TTPenjualan)
            val TPEN = TPenjualan.substring(0, 2) + " " + TPenjualan.substring(2, TPenjualan.length)
            val TPembelian = Rp.format(TTPembelian)
            val TPEM = TPembelian.substring(0, 2) + " " + TPembelian.substring(2, TPembelian.length)

            tv_total_pen.text = TPEN
            tv_total_pem.text = TPEM
            ("$JTPenjualan Transaksi").also { totaltransaksi_nama.text = it }
            ("$JTPPembelian Transaksi").also { totaltransaksi_nama2.text = it }

            for (snapPembelian in ssPembelian.children) {
                if (awal == -1L && akhir == -1L) {
                    for (snapTransaksi in ssPenjualan.children) {
                        val t = snapTransaksi.getValue(classRiwayat::class.java)
                        cRiwayat.add(t!!)
                    }
                } else {
                    for (snapTransaksi in ssPenjualan.children) {
                        val t = snapTransaksi.getValue(classRiwayat::class.java)
                        if (t?.tanggal!! >= awal && t.tanggal!! <= akhir) {
                            cRiwayat.add(t)
                        }
                    }
                }
            }

            rv_riwayat.adapter = adapterRiwayat(cRiwayat)
        }
    }
}