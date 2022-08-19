package com.project.sikasir.pembelian

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import kotlinx.android.synthetic.main.kelolapembelian.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class kelolaPembelian : AppCompatActivity() {
    val Rp = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    val waktu = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date())
    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""
    var email_Pegawai = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kelolapembelian)
        getUsernameLocal()

        val namaProduk: String = intent.getStringExtra("Nama_Produk").toString()
        val HModal: String = intent.getStringExtra("harga_modal").toString()
        val stok: String = intent.getStringExtra("stok").toString()

        minus.isEnabled = false
        kp_tv_tgl.text = waktu

        if (stok.isNotEmpty() || stok == "null") {
            tv_sStok.text = stok
        }
        if (namaProduk.isNotEmpty() || namaProduk == "null") {
            kp_tv_nama.text = namaProduk
        }

        if (HModal.isEmpty() || HModal == "null" || HModal == "Rp " || HModal == "0") {
            ed_harga_kosong.isEnabled = true
        } else {
            ed_harga_kosong.isEnabled = false
            ed_harga_kosong.setText(HModal)
            btn_ubah_harga.visibility = View.VISIBLE
        }

        btn_ubah_harga.setOnClickListener {
            ed_harga_kosong.isEnabled = true
            btn_ubah_harga.visibility = View.GONE
        }

        plus.setOnClickListener {
            minus.isEnabled = true
            if (tv_jml_pembelian.text.toString().isEmpty()) {
                tv_jml_pembelian.text = "1"
            } else {
                jumlah(tv_jml_pembelian.text.toString().toInt() + 1)
            }
        }
        minus.setOnClickListener {
            if (tv_jml_pembelian.text.toString().toInt() <= 0) {
                minus.isEnabled = false
                kp_total.error = "Jumlah Produk tidak bisa 0"
            } else {
                jumlah(tv_jml_pembelian.text.toString().toInt() - 1)
            }
        }

        tv_jml_pembelian.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ed_harga_kosong.text.toString().replace(",00", "").filter { it.isDigit() }.isNotEmpty()) {
                    totalPembelian(ed_harga_kosong.text.toString().replace(",00", "").filter { it.isDigit() }.toInt() * tv_jml_pembelian.text.toString().toInt())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btn_simpan_pembelian.setOnClickListener {
            if (tv_jml_pembelian.text.toString() == "0") {
                Toast.makeText(this, "Minimal Pembelian 1 Stok", Toast.LENGTH_SHORT).show()
            } else {
                if (ed_harga_kosong.isEnabled) {
                    simpanPembelian()
                } else {
                    if (ed_harga_kosong.text.toString().any { it.isDigit() }) {
                        simpanPembelian()
                    } else {
                        ed_harga_kosong.error = "isi harga terlebih dahulu"
                    }
                }
            }
        }
    }

    private fun simpanPembelian() {
        val id_Produk: String = intent.getStringExtra("id_Produk").toString()
        val stok: String = intent.getStringExtra("stok").toString()

        val keterangan: String = kp_edt_keterangan.text.toString()
        val EdHarga: String = ed_harga_kosong.text.toString().replace(",00", "")
        val jumlah: String = tv_jml_pembelian.text.toString()
        val total: String = kp_total.text.toString()
        val id = Calendar.getInstance().time.time

        val refPembelian = FirebaseDatabase.getInstance().reference.child("Pembelian").child(waktu)
        val refDetailPembelian = FirebaseDatabase.getInstance().reference.child("DetailPembelian").child(waktu)

        refPembelian.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapPembelian: DataSnapshot) {
                if (!snapPembelian.exists()) {
                    snapPembelian.ref.child("id_pembelian").setValue(id)
                    snapPembelian.ref.child("tanggal").setValue(waktu)
                    snapPembelian.ref.child("totalPembelian").setValue(total)
                    snapPembelian.ref.child("keterangan").setValue(keterangan)
                    snapPembelian.ref.child("email_Pegawai").setValue(email_Pegawai)
                    snapPembelian.ref.child("harga_Modal").setValue(Rp.format(EdHarga.filter { it.isDigit() }.toInt()))
                    updateProduk(id_Produk, Rp.format(EdHarga.filter { it.isDigit() }.toInt()), (stok.toInt() + jumlah.toInt()).toString())
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {}
        })

        refDetailPembelian.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapPembelian: DataSnapshot) {
                if (!snapPembelian.exists()) {
                    snapPembelian.ref.child("id_detailPembelian").setValue(id)
                    snapPembelian.ref.child("jumlah_Produk").setValue(jumlah)
                    snapPembelian.ref.child("total").setValue(total)
                    snapPembelian.ref.child("id_Produk").setValue(id_Produk)
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {}
        })
        finish()
        Toast.makeText(this, "Data pembelian berhasil ditambahkan", Toast.LENGTH_SHORT).show()
    }

    private fun updateProduk(id_Produk: String, Harga_Modal: String, Stok: String) {
        val refProduk = FirebaseDatabase.getInstance().getReference("Produk")

        val produk = mapOf<String, String>(
            "harga_Modal" to Harga_Modal,
            "stok" to Stok
        )

        refProduk.child(id_Produk).updateChildren(produk)
    }

    private fun getUsernameLocal() {
        val sharedPreference: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()

        val reference = FirebaseDatabase.getInstance().reference.child("Pegawai").child(username_key_new)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                email_Pegawai = dataSnapshot.child("Email_Pegawai").value.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun totalPembelian(hasil: Int) {
        val totalString = Rp.format(hasil)
        val total = totalString.substring(0, 2) + " " + totalString.substring(2, totalString.length)
        kp_total.text = total
    }

    private fun jumlah(hasil: Int) {
        tv_jml_pembelian.text = hasil.toString()
    }

}