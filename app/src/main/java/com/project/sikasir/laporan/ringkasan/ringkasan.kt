package com.project.sikasir.laporan.ringkasan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import com.project.sikasir.laporan.pembelian.laporanPembelian
import com.project.sikasir.laporan.penjualan.laporanPenjualan
import com.project.sikasir.pembelian.classPembelian
import com.project.sikasir.penjualan.pembayaran.classPenjualan
import kotlinx.android.synthetic.main.laporan_ringkasan.*
import java.text.NumberFormat
import java.util.*
import kotlin.math.abs

class ringkasan : AppCompatActivity() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    private lateinit var ssPenjualan: DataSnapshot
    private lateinit var ssPembelian: DataSnapshot

    private var awal = -1L
    private var akhir = -1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laporan_ringkasan)

        cl_pemb.setOnClickListener { startActivity(Intent(this, laporanPembelian::class.java)) }
        cl_pen.setOnClickListener { startActivity(Intent(this, laporanPenjualan::class.java)) }
        getData()
        editTextDate3.setOnClickListener { tgl() }
        tvA3toA5.setOnClickListener { finish() }
    }

    private fun tgl() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(supportFragmentManager, "DatePicker")

        editTextDate3.isEnabled = false
        Toast.makeText(this, "tunggu sebentar", Toast.LENGTH_SHORT).show()

        datePicker.addOnPositiveButtonClickListener {
            awal = datePicker.selection!!.first
            akhir = datePicker.selection!!.second

            editTextDate3.setText(datePicker.headerText)
            editTextDate3.isEnabled = true
            initAdapter()
        }

        datePicker.addOnNegativeButtonClickListener {
            editTextDate3.isEnabled = true
        }

        datePicker.addOnCancelListener {
            editTextDate3.isEnabled = true
        }
    }

    private fun getData() {
        val refPenjualan = FirebaseDatabase.getInstance().getReference("Penjualan")
        val refPembelian = FirebaseDatabase.getInstance().getReference("Pembelian")
        refPenjualan.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshotTransaksi: DataSnapshot) {
                if (snapshotTransaksi.exists()) {
                    refPembelian.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshotPembelian: DataSnapshot) {
                            if (snapshotTransaksi.exists()) {
                                ssPenjualan = snapshotTransaksi
                                ssPembelian = snapshotPembelian
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
        if (this::ssPenjualan.isInitialized && this::ssPenjualan.isInitialized) {
            var penjualanKotor = 0
            var diskonvar = 0
            var totPembelian = 0

            if (awal == -1L && akhir == -1L) {
                for (snapPenjualan in ssPenjualan.children) {
                    val t = snapPenjualan.getValue(classPenjualan::class.java)
                    penjualanKotor += t?.total?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                    diskonvar += t.diskon?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                }
            } else {
                for (snapPenjualan in ssPenjualan.children) {
                    val t = snapPenjualan.getValue(classPenjualan::class.java)
                    if (t?.tanggal!! >= awal && t.tanggal!! <= akhir) {
                        penjualanKotor += t.total?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                        diskonvar += t.diskon?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                    }
                }
            }
            if (awal == -1L && akhir == -1L) {
                for (snapBeli in ssPembelian.children) {
                    val t = snapBeli.getValue(classPembelian::class.java)
                    totPembelian += t?.totalPembelian?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                }
            } else {
                for (snapBeli in ssPembelian.children) {
                    val t = snapBeli.getValue(classPembelian::class.java)
                    if (t?.id_pembelian!! >= awal && t.id_pembelian!! <= akhir) {
                        totPembelian += t.totalPembelian?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                    }
                }
            }

            val tpenjualan = penjualanKotor - diskonvar
            var tKeutungan = tpenjualan - totPembelian
            if (tKeutungan < 0) {
                "Kerugian ".also { textView60.text = it }
                tKeutungan = abs(tKeutungan)
                textView60.setTextColor(Color.parseColor("#FF1744"))
                textView61.setTextColor(Color.parseColor("#FF1744"))
            } else {
                "Keuntungan ".also { textView60.text = it }
                textView60.setTextColor(Color.parseColor("#1DE9B6"))
                textView61.setTextColor(Color.parseColor("#1DE9B6"))
            }

            val b = Rp.format(penjualanKotor)
            val total = b.substring(0, 2) + " " + b.substring(2, b.length)
            textView32.text = total

            val a = Rp.format(diskonvar)
            val diskon = a.substring(0, 2) + " " + a.substring(2, a.length)
            tv_tp_diskon.text = diskon

            val s = Rp.format(tpenjualan)
            val totalpenjualan = s.substring(0, 2) + " " + s.substring(2, s.length)
            tv_tpenjualan.text = totalpenjualan
            textView57.text = totalpenjualan

            val c = Rp.format(totPembelian)
            val modal = c.substring(0, 2) + " " + c.substring(2, c.length)
            tv_pembkot.text = modal
            tv_total_pembelian.text = modal
            tv_tot_pemb.text = modal

            val d = Rp.format(tKeutungan)
            val keuntungan = d.substring(0, 2) + " " + d.substring(2, d.length)
            textView61.text = keuntungan
        }
    }
}