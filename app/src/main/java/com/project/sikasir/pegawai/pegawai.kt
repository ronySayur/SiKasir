package com.project.sikasir.pegawai

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import com.project.sikasir.R
import com.project.sikasir.listview.classPegawai
import kotlinx.android.synthetic.main.pegawai_menu.*

class pegawai : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var pegawaiList: ArrayList<classPegawai>
    private val produkAdapter: adapterSearchPegawai by lazy {
        adapterSearchPegawai(pegawaiList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pegawai_menu)
        //Set
        getDataPegawai()
        onClick()
        cariPegawai()
    }

    private fun cariPegawai() {
        edcaripegawai.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    getDataPegawai()
                } else {
                    pegawaiList.clear()
                    searchByName(edcaripegawai.text.toString())
                }
            }
        })
    }

    private fun onClick() {
        tvA5toA2.setOnClickListener { finish() }
        fabTambahPegawai.setOnClickListener { startActivity(Intent(this, kelolaPegawai::class.java)) }
    }

    private fun getDataPegawai() {
        rv_pegawai.layoutManager = GridLayoutManager(this, 2)
        rv_pegawai.setHasFixedSize(true)
        pegawaiList = arrayListOf<classPegawai>()

        dbref = FirebaseDatabase.getInstance().getReference("Pegawai")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rv_pegawai.visibility = View.VISIBLE
                    g_kosong.visibility = View.GONE
                    pegawaiList.clear()
                    for (userSnapshot in snapshot.children) {
                        val classPegawai = userSnapshot.getValue(classPegawai::class.java)
                        pegawaiList.add(classPegawai!!)
                    }
                    rv_pegawai.adapter = adapterPegawai(pegawaiList)
                } else {
                    rv_pegawai.visibility = View.GONE
                    g_kosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun searchByName(search: String) {
        val refSearch = FirebaseDatabase.getInstance().getReference("Pegawai")
        refSearch.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        val pegawai = i.getValue(classPegawai::class.java)
                        if (pegawai!!.Nama_Pegawai == search) {
                            pegawaiList.add(pegawai)
                        }
                    }
                    produkAdapter.submitList(pegawaiList)
                    rv_pegawai.adapter = adapterPegawai(pegawaiList)
                } else {
                    Toast.makeText(applicationContext, "Data does not exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}