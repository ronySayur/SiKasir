package com.project.sikasir.penjualan.pembayaran

data class classPenjualan(
    var tanggal: Long? = 0,
    var emailPegawai: String? = null,
    var namaPegawai: String? = null,
    var jabatan: String? = null,
    var total: String? = null,
    var diskon: String? = null,
    var total_Modal: String? = null,
    var detailTransaksi: String? = null
)