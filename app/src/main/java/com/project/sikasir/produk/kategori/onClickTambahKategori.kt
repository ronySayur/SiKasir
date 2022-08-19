package com.project.sikasir.produk.kategori

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*
import com.project.sikasir.R
import kotlinx.android.synthetic.main.dialog_tambah.*
import kotlinx.android.synthetic.main.dialog_tambah.view.*

/**
 * Dibuat oleh RizkyPratama pada 21-Apr-22.
 */
class onClickTambahKategori : DialogFragment() {
    private lateinit var reference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Inflater
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        val view: View = inflater.inflate(R.layout.dialog_tambah, container, false)

        //Set
        view.edNama.hint = "Nama Kategori"
        view.tv_hapus.visibility = View.GONE

        //Tangkap
        val bundle = arguments
        val ed = bundle?.getString("Edit").toString()
        val Nama_Kategori = bundle?.getString("Nama_Kategori").toString()

        if (ed == "null") {
            view.tv_hapus.visibility = View.GONE
            view.tv_batal.visibility = View.GONE
        } else {
            view.edNama.setText(Nama_Kategori)
            view.tv_hapus.visibility = View.VISIBLE
        }

        view.tv_hapus.setOnClickListener { deleteKategori() }
        view.tv_batal.setOnClickListener { navigateToBackStack() }
        view.tvSimpan.setOnClickListener { simpanKategori() }

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun navigateToBackStack() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
        requireActivity().supportFragmentManager.popBackStack()
    }

    fun simpanKategori() {
        if (edNama.text.toString().isEmpty()) {
            edNama.error = "Isi nama Kategori terlebih dahulu"
        } else {
            reference = FirebaseDatabase.getInstance().reference.child("Kategori").child(edNama.text.toString())
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val alert = AlertDialog.Builder(this@onClickTambahKategori.requireActivity())
                        alert.setTitle("Peringatan")
                        alert.setMessage("Data dengan nama " + edNama.text.toString() + " Sudah Ada!")
                        alert.setPositiveButton("Ok", null)
                        alert.show()
                    } else {
                        dataSnapshot.ref.child("Nama_Kategori").setValue(edNama.text.toString())
                        Toast.makeText(this@onClickTambahKategori.requireActivity(), "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                        navigateToBackStack()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    fun deleteKategori() {
        reference = FirebaseDatabase.getInstance().reference.child("Kategori").child(edNama.text.toString())
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.removeValue()
                    }
                    Toast.makeText(this@onClickTambahKategori.requireActivity(), "Data berhasil terhapus", Toast.LENGTH_SHORT).show()
                    navigateToBackStack()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}