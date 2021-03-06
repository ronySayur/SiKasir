package com.project.sikasir.transaksi.transaksi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.project.sikasir.R
import com.project.sikasir.transaksi.keranjang.adapterKeranjang
import com.project.sikasir.transaksi.keranjang.classKeranjang
import com.project.sikasir.transaksi.pembayaran.classPembayaran
import kotlinx.android.synthetic.main.transaksi_berhasil.*
import java.text.NumberFormat
import java.util.*


class transaksiBerhasil : AppCompatActivity() {

    private var printing: Printing? = null
    val keranjangList = ArrayList<classKeranjang>()

    lateinit var currentDate: String
    lateinit var nama: String
    lateinit var jabatan: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaksi_berhasil)

        set()/*
        button2.setOnClickListener {
            startActivity(Intent(this, transaksi::class.java))
            finish()
        }*/
        getKeranjang()
    }

    private fun set() {
        val Diterima: String = this.intent.getStringExtra("Diterima").toString()
        val Total_Tagihan: String = this.intent.getStringExtra("Total_Tagihan").toString().replace(",00", "").replace(".", "").replace("Rp ", "")

        currentDate = this.intent.getStringExtra("Tanggalan").toString()
        nama = this.intent.getStringExtra("Pegawai").toString()
        jabatan = this.intent.getStringExtra("Jabatan").toString()

        val kembalian: String = this.intent.getStringExtra("Kembalian").toString()

        println(Diterima + " " + kembalian)

        tv_tgl.text = currentDate
        tv_jabatan.text = jabatan
        tv_namapegawai.text = nama

        tv_diterima.text = Diterima

        if (Total_Tagihan == Diterima) {
            textView42.visibility = View.GONE
            tv_kembalian.visibility = View.GONE
        } else {
            textView42.visibility = View.VISIBLE
            tv_kembalian.visibility = View.VISIBLE
/*       tv_kembalian.text = */
        }
    }

    private fun print() {
        Printooth.init(this)
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initListeners()
    }

    private fun initListeners() {
        cetak_struk.setOnClickListener {
            if (!Printooth.hasPairedPrinter()) startActivityForResult(
                Intent(this, ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER
            )
            else printSomePrintable()
        }

        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@transaksiBerhasil, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@transaksiBerhasil, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@transaksiBerhasil, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@transaksiBerhasil, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@transaksiBerhasil, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@transaksiBerhasil, "Disconnected Printer", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun printSomePrintable() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }


    private fun getSomePrintables() = ArrayList<Printable>().apply {
        add(RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()) // feed lines example in raw mode

        add(
            TextPrintable.Builder()
                .setText("Aida Putra \nJl. Mayjen. Sutoyo No.4, Kab. Bantul, Daerah Istimewa Yogyakarta 55711\n\n-----------------------------")
                .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Pembayaran:")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText(tv_namapegawai.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Total Tagihan:")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText(tv_totaltagihan.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Diterima:")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText(tv_diterima.text.toString())
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )

        add(
            TextPrintable.Builder()
                .setText("Kembalian:")
                .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText(tv_kembalian.text.toString() + "\n\n")
                .setAlignment(DefaultPrinter.ALIGNMENT_RIGHT)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
                .setNewLinesAfter(1)
                .build()
        )
    }

    private fun getKeranjang() {
        rv_complete.layoutManager = GridLayoutManager(this, 1)
        rv_complete.setHasFixedSize(true)

        val refKeranjang = FirebaseDatabase.getInstance().getReference("Keranjang")

        refKeranjang.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rv_complete.visibility = View.VISIBLE
                    cl_keranjang_kosong.visibility = View.GONE

                    keranjangList.clear()

                    var totalKeranjang = 0
                    var modal = 0
                    var diskon = 0
                    val refTransaksi = FirebaseDatabase.getInstance().getReference("Transaksi")

                    val cPembayaran = classPembayaran(ServerValue.TIMESTAMP, nama, jabatan)

                    for (Keranjang in snapshot.children) {
                        //DataKeranjang
                        val k = Keranjang.getValue(classKeranjang::class.java)
                        keranjangList.add(k!!)
                        cPembayaran.produk.add(k)

                        //Total Modal di Keranjang
                        if (Keranjang.child("total_Modal").exists() && Keranjang.child("total_Modal").toString() == "Rp") {
                            modal += Integer.parseInt(Keranjang.child("total_Modal").getValue(String::class.java)!!.replace(",00", "").replace(".", "").replace("Rp ", ""))
                        }
                        //Total Harga di Keranjang
                        if (Keranjang.child("total").exists()) {
                            totalKeranjang += Integer.parseInt(Keranjang.child("total").getValue(String::class.java)!!.replace(",00", "").replace(".", "").replace("Rp ", ""))
                        }
                        //Total Diskon di Keranjang
                        if (Keranjang.child("diskon").exists()) {
                            diskon += Integer.parseInt(Keranjang.child("diskon").getValue(String::class.java)!!.replace(",00", "").replace(".", "").replace("Rp ", ""))
                        }
                    }

                    val m = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(modal)
                    val mod = m.substring(0, 2) + " " + m.substring(2, m.length)
                    val k = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(totalKeranjang)
                    val total = k.substring(0, 2) + " " + k.substring(2, k.length)
                    val d = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(diskon)
                    val disk = d.substring(0, 2) + " " + d.substring(2, d.length)

                    tv_totaltagihan.text = total
                    cPembayaran.diskon = disk
                    cPembayaran.total = total
                    cPembayaran.total_Modal = mod
                    rv_complete.adapter = adapterKeranjang(keranjangList)

                    //push dari class pembayaran ke firebase
                    refTransaksi.push().setValue(cPembayaran)
                    refKeranjang.removeValue()
                    Toast.makeText(this@transaksiBerhasil, "Transaksi Berhasil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}