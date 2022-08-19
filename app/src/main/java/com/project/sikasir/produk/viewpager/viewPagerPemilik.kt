package com.project.sikasir.produk.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.sikasir.pembelian.infoPembelian
import com.project.sikasir.pembelian.pembelian
import com.project.sikasir.produk.produk.produk

/**
 * Dibuat oleh RizkyPratama pada 12-Apr-22.
 */

class viewPagerPemilik(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        //3 jumlah tabs
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return produk()
            1 -> return pembelian()
            2 -> return infoPembelian()
        }
        return produk()
    }
}
