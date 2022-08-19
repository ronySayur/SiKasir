package com.project.sikasir.supplier

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.sikasir.R
import kotlinx.android.synthetic.main.supplier_menu.*

class supplier : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supplier_menu)
        getSupplier()
        fabTambahSup.setOnClickListener { startActivity(Intent(this, kelolaSupplier::class.java)) }
    }

    private fun getSupplier() {
        rv_sup.layoutManager = GridLayoutManager(this, 1)
        rv_sup.setHasFixedSize(true)
        val SupList = arrayListOf<classSupplier>()
        val dbref = FirebaseDatabase.getInstance().getReference("Supplier")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rv_sup.visibility = View.VISIBLE
                    g_kosong.visibility = View.GONE
                    SupList.clear()
                    for (supSnap in snapshot.children) {
                        val s = supSnap.getValue(classSupplier::class.java)
                        SupList.add(s!!)
                    }
                    rv_sup.adapter = adapterSupplier(SupList)
                } else {
                    rv_sup.visibility = View.GONE
                    g_kosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}