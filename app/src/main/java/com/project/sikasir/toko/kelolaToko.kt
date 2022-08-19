package com.project.sikasir.toko

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import kotlinx.android.synthetic.main.kelola_toko.*
import java.util.*

class kelolaToko : AppCompatActivity() {
    val kode = Calendar.getInstance().time.time.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kelola_toko)
        cv_hapus.setOnClickListener { hapusToko() }

        val nama_Toko: String = intent.getStringExtra("nama_Toko").toString()
        if (nama_Toko.isEmpty() || nama_Toko == "null") {
            tambahToko()
        } else {
            editToko()
        }
    }

    private fun tambahToko() {
        cv_hapus.visibility = View.GONE
        textView88.setOnClickListener(View.OnClickListener {
            if (edNamaToko.text.isEmpty()) {
                edNamaToko.error = "Nama Toko tidak boleh kosong"
            } else {
                if (edAlamatToko.text.isEmpty()) {
                    edAlamatToko.error = "Alamat Toko tidak boleh kosong"
                } else {
                    if (ednoTelp.text.toString().isEmpty()) {
                        ednoTelp.error = "Nomor Telepon tidak boleh kosong"
                    } else {
                        if (edtEmailToko.text.toString().isValidEmail()) {
                            val reference = FirebaseDatabase.getInstance().reference.child("Toko").child(kode)
                            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        val alert = AlertDialog.Builder(this@kelolaToko)
                                        alert.setTitle("Peringatan")
                                        alert.setMessage("Data toko dengan nama " + edNamaToko.text.toString() + " Sudah Ada!")
                                        alert.setPositiveButton("Ok", null)
                                        alert.show()
                                    } else {
                                        dataSnapshot.ref.child("id_Toko").setValue(kode)
                                        dataSnapshot.ref.child("nama_Toko").setValue(edNamaToko.text.toString())
                                        dataSnapshot.ref.child("alamat").setValue(edAlamatToko.text.toString())
                                        dataSnapshot.ref.child("no_Telp").setValue(ednoTelp.text.toString())
                                        dataSnapshot.ref.child("email").setValue(edtEmailToko.text.toString())
                                        Toast.makeText(this@kelolaToko, edNamaToko.text.toString() + " Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                }

                                override fun onCancelled(dataSnapshot: DatabaseError) {}
                            })
                        } else {
                            edtEmailToko.error = "Format email tidak benar"
                        }
                    }
                }
            }
        })
    }

    private fun editToko() {
        val nama_Toko: String = intent.getStringExtra("nama_Toko").toString()
        val alamat: String = intent.getStringExtra("alamat").toString()
        val no_Telp: String = intent.getStringExtra("no_Telp").toString()
        val email: String = intent.getStringExtra("email").toString()
        val id_Toko: String = intent.getStringExtra("id_Toko").toString()

        edNamaToko.setText(nama_Toko)
        edAlamatToko.setText(alamat)
        ednoTelp.setText(no_Telp)
        edtEmailToko.setText(email)

        cv_hapus.visibility = View.VISIBLE

        textView88.setOnClickListener(View.OnClickListener {
            if (edNamaToko.text.isEmpty()) {
                edNamaToko.error = "Nama Toko tidak boleh kosong"
            } else {
                if (edAlamatToko.text.isEmpty()) {
                    edAlamatToko.error = "Alamat Toko tidak boleh kosong"
                } else {
                    if (ednoTelp.text.toString().isEmpty()) {
                        ednoTelp.error = "Nomor Telepon tidak boleh kosong"
                    } else {
                        if (edtEmailToko.text.toString().isValidEmail()) {
                            val reference = FirebaseDatabase.getInstance().reference.child("Toko")

                            val toko = mapOf<String, String>(
                                "nama_Toko" to edNamaToko.text.toString(),
                                "alamat" to edAlamatToko.text.toString(),
                                "no_Telp" to ednoTelp.text.toString(),
                                "email" to edtEmailToko.text.toString()
                            )
                            reference.child(id_Toko).updateChildren(toko)

                            finish()
                            Toast.makeText(this@kelolaToko, nama_Toko + " Berhasil dirubah", Toast.LENGTH_SHORT).show()
                        } else {
                            edtEmailToko.error = "Format email tidak benar"
                        }
                    }
                }
            }
        })
    }

    private fun hapusToko() {
        val id_toko: String = intent.getStringExtra("id_Toko").toString()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Yakin Untuk Menghapus Data?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                val reference = FirebaseDatabase.getInstance().reference.child("Toko").child(id_toko)
                reference.removeValue()
                finish()
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }


    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()
}