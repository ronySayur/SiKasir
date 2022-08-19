package com.project.sikasir.laporan.pembelian

data class classLapPembelian(
    var nama_Vendor: String? = null,
    var no_Vendor: String? = null,
    var email_Vendor: String? = null,
    var nama_PIC: String? = null,
    var noPIC: String? = null,
    var keterangan: String? = null,
    var totalPembelian: Int? = 0,
    var jumlahTransaksi: Int? = 0
)