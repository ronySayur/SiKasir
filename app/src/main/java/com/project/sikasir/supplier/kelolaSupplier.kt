package com.project.sikasir.supplier

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
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
import kotlinx.android.synthetic.main.kelola_supplier.*

class kelolaSupplier : AppCompatActivity() {
    private var USERNAME_KEY = "username_key"
    private var username_key = ""
    private var username_key_new = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kelola_supplier)
        getUsernameLocal()
        cv_hapus.setOnClickListener { hapusSupplier() }

        val nama_Vendor: String = intent.getStringExtra("nama_Vendor").toString()
        if (nama_Vendor.isEmpty() || nama_Vendor == "null") {
            tambahSupplier()
        } else {
            editSup()
        }
    }

    private fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun editSup() {
        val nama_Vendor: String = intent.getStringExtra("nama_Vendor").toString()
        val alamat_Vendor: String = intent.getStringExtra("alamat_Vendor").toString()
        val no_Vendor: String = intent.getStringExtra("no_Vendor").toString()
        val email_Vendor: String = intent.getStringExtra("email_Vendor").toString()
        val nama_PIC: String = intent.getStringExtra("nama_PIC").toString()
        val jabatan_PIC: String = intent.getStringExtra("jabatan_PIC").toString()
        val No_PIC: String = intent.getStringExtra("noPIC").toString()

        edtNamaVendor.setText(nama_Vendor)
        edtAlamatSupplier.setText(alamat_Vendor)
        edtEmailSupplier.setText(email_Vendor)
        edtHpVendor.setText(no_Vendor)
        ed_PIC.setText(nama_PIC)
        ed_jabatanPIC.setText(jabatan_PIC)
        edNoPIC.setText(No_PIC)

        cv_hapus.visibility = View.VISIBLE

        textView88.setOnClickListener(View.OnClickListener {
            if (edtNamaVendor.text.isEmpty()) {
                edtNamaVendor.error = "Nama Vendor tidak boleh kosong"
            } else {
                if (edtHpVendor.text.isEmpty()) {
                    edtHpVendor.error = "Nomor HP Vendor tidak boleh kosong"
                } else {
                    if (edtAlamatSupplier.text.toString().isEmpty()) {
                        edtAlamatSupplier.error = "Alamat Vendor tidak boleh kosong"
                    } else {
                        if (ed_PIC.text.toString().isEmpty()) {
                            ed_PIC.error = "Nama PIC harus diisi"
                        } else {
                            if (ed_jabatanPIC.text.toString().isEmpty()) {
                                ed_jabatanPIC.error = "Jabatan PIC harus diisi"
                            } else {
                                if (edNoPIC.text.toString().isEmpty()) {
                                    edNoPIC.error = "Nomor Penganggung jawab harus diisi"
                                } else {
                                    if (edtEmailSupplier.text.toString().isValidEmail()) {
                                        val reference = FirebaseDatabase.getInstance().reference.child("Supplier")

                                        val supplier = mapOf<String, String>(
                                            "email_Vendor" to edtEmailSupplier.text.toString(),
                                            "nama_Vendor" to edtNamaVendor.text.toString(),
                                            "alamat_Vendor" to edtAlamatSupplier.text.toString(),
                                            "no_Vendor" to edtHpVendor.text.toString(),
                                            "nama_PIC" to ed_PIC.text.toString(),
                                            "jabatan_PIC" to ed_jabatanPIC.text.toString(),
                                            "noPIC" to edNoPIC.text.toString()
                                        )
                                        reference.child(edtEmailSupplier.text.toString().replace(".", ",")).updateChildren(supplier)

                                        finish()
                                        Toast.makeText(this@kelolaSupplier, edtEmailSupplier.text.toString() + " Berhasil dirubah", Toast.LENGTH_SHORT).show()
                                    } else {
                                        edtEmailSupplier.error = "Format email tidak benar"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun tambahSupplier() {
        cv_hapus.visibility = View.GONE
        textView88.setOnClickListener(View.OnClickListener {
            if (edtNamaVendor.text.isEmpty()) {
                edtNamaVendor.error = "Nama Vendor tidak boleh kosong"
            } else {
                if (edtHpVendor.text.isEmpty()) {
                    edtHpVendor.error = "Nomor HP Vendor tidak boleh kosong"
                } else {
                    if (edtAlamatSupplier.text.toString().isEmpty()) {
                        edtAlamatSupplier.error = "Alamat Vendor tidak boleh kosong"
                    } else {
                        if (ed_PIC.text.toString().isEmpty()) {
                            ed_PIC.error = "Nama penanggung jawab harus diisi"
                        } else {
                            if (ed_jabatanPIC.text.toString().isEmpty()) {
                                ed_jabatanPIC.error = "Jabatan PIC harus diisi"
                            } else {
                                if (edNoPIC.text.toString().isEmpty()) {
                                    edNoPIC.error = "Nomor Penganggung jawab harus diisi"
                                } else {
                                    if (edtEmailSupplier.text.toString().isValidEmail()) {
                                        val reference = FirebaseDatabase.getInstance().reference.child("Supplier").child(edtEmailSupplier.text.toString().replace(".", ","))
                                        reference.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    val alert = AlertDialog.Builder(this@kelolaSupplier)
                                                    alert.setTitle("Peringatan")
                                                    alert.setMessage("Data dengan email " + edtEmailSupplier.text.toString() + " Sudah Ada!")
                                                    alert.setPositiveButton("Ok", null)
                                                    alert.show()
                                                } else {
                                                    dataSnapshot.ref.child("email_Vendor").setValue(edtEmailSupplier.text.toString())
                                                    dataSnapshot.ref.child("nama_Vendor").setValue(edtNamaVendor.text.toString())
                                                    dataSnapshot.ref.child("no_Vendor").setValue(edtHpVendor.text.toString())
                                                    dataSnapshot.ref.child("alamat_Vendor").setValue(edtAlamatSupplier.text.toString())
                                                    dataSnapshot.ref.child("nama_PIC").setValue(ed_PIC.text.toString())
                                                    dataSnapshot.ref.child("jabatan_PIC").setValue(ed_jabatanPIC.text.toString())
                                                    dataSnapshot.ref.child("noPIC").setValue(edNoPIC.text.toString())
                                                    Toast.makeText(this@kelolaSupplier, edtEmailSupplier.text.toString() + " Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                                                    finish()
                                                }
                                            }

                                            override fun onCancelled(dataSnapshot: DatabaseError) {}
                                        })
                                    } else {
                                        edtEmailSupplier.error = "Format email tidak benar"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun hapusSupplier() {
        val builder = AlertDialog.Builder(this@kelolaSupplier)
        builder.setMessage("Yakin Untuk Menghapus Data?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                val reference = FirebaseDatabase.getInstance().reference.child("Supplier").child(edtEmailSupplier.text.toString().replace(".", ","))
                reference.removeValue()
                finish()
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun getUsernameLocal() {
        val sharedPreference: SharedPreferences = getSharedPreferences(USERNAME_KEY, Context.MODE_PRIVATE)
        username_key_new = sharedPreference.getString(username_key, "").toString()
    }
}