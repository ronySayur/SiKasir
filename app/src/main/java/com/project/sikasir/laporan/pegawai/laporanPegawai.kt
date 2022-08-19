package com.project.sikasir.laporan.pegawai

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
import com.project.sikasir.listview.classPegawai
import com.project.sikasir.penjualan.pembayaran.classPenjualan
import kotlinx.android.synthetic.main.laporan_pegawai.*
import kotlinx.android.synthetic.main.laporan_rangkuman.*
import java.text.NumberFormat
import java.util.*

class laporanPegawai : AppCompatActivity() {
    val classLapPegawai = ArrayList<classLapPegawai>()
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    private lateinit var ssTransaksi: DataSnapshot
    private lateinit var ssPegawai: DataSnapshot

    private var awal = -1L
    private var akhir = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laporan_pegawai)

        tvA3toA4.setOnClickListener { finish() }

        getData()

        editTextDate2.setOnClickListener { tgl() }
    }

    private fun tgl() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        datePicker.show(supportFragmentManager, "DatePicker")

        editTextDate2.isEnabled = false
        Toast.makeText(this, "tunggu sebentar", Toast.LENGTH_SHORT).show()

        datePicker.addOnPositiveButtonClickListener {
            awal = datePicker.selection!!.first
            akhir = datePicker.selection!!.second

            editTextDate2.setText(datePicker.headerText)
            editTextDate2.isEnabled = true
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
        rv_peg.layoutManager = GridLayoutManager(this, 1)
        rv_peg.setHasFixedSize(true)

        val refTransaksi = FirebaseDatabase.getInstance().getReference("Penjualan").orderByValue()
        val refPegawai = FirebaseDatabase.getInstance().getReference("Pegawai")

        refPegawai.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotPegawai: DataSnapshot) {
                if (snapshotPegawai.exists()) {

                    refTransaksi.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshotTransaksi: DataSnapshot) {
                            if (snapshotTransaksi.exists()) {
                                ssPegawai = snapshotPegawai
                                ssTransaksi = snapshotTransaksi
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
        if (this::ssTransaksi.isInitialized && this::ssPegawai.isInitialized) {
            var jumlahTransaksi = 0
            var totalTransaksi = 0
            classLapPegawai.clear()

            if (awal == -1L && akhir == -1L) {
                for (snapTransaksi in ssTransaksi.children) {
                    val t = snapTransaksi.getValue(classPenjualan::class.java)

                    jumlahTransaksi += 1
                    totalTransaksi += t?.total?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                }
            } else {
                for (snapTransaksi in ssTransaksi.children) {
                    val t = snapTransaksi.getValue(classPenjualan::class.java)

                    if (t?.tanggal!! >= awal && t.tanggal!! <= akhir) {
                        jumlahTransaksi += 1
                        totalTransaksi += t.total?.replace(",00", "")?.filter { it.isDigit() }?.toInt()!!
                    }
                }
            }

            val totalString = Rp.format(totalTransaksi)
            val total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)
            tv_tot_pembelian.text = total
            tv_jmltransaksi.text = jumlahTransaksi.toString()

            for (snapPegawai in ssPegawai.children) {
                val p = snapPegawai.getValue(classPegawai::class.java)
                val lp = classLapPegawai(p?.Nama_Pegawai, p?.Nama_Jabatan)

                if (awal == -1L && akhir == -1L) {
                    for (snapTransaksi in ssTransaksi.children) {
                        val t = snapTransaksi.getValue(classPenjualan::class.java)

                        //jika orang sama tambah omset
                        if (t?.emailPegawai.equals(p?.Email_Pegawai)) {
                            lp.jumlahTransaksi = lp.jumlahTransaksi?.plus(1)
                            lp.omset = t?.total?.replace(",00", "")?.filter { it.isDigit() }?.let { lp.omset?.plus(it.toInt()) }
                        }
                    }
                } else {
                    for (snapTransaksi in ssTransaksi.children) {
                        val t = snapTransaksi.getValue(classPenjualan::class.java)
                        if (t?.emailPegawai.equals(p?.Email_Pegawai) && t?.tanggal!! >= awal && t.tanggal!! <= akhir) {
                            lp.jumlahTransaksi = lp.jumlahTransaksi?.plus(1)
                            lp.omset =
                                t.total?.replace(",00", "")?.filter { it.isDigit() }?.let {
                                    lp.omset?.plus(it.toInt())
                                }
                        }
                    }
                }
                classLapPegawai.add(lp)
            }
            rv_peg.adapter = adapterLaporanPegawai(classLapPegawai)
        }
    }
}
