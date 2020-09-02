package com.anvesh.nogozoshopapplication.datamodels

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class VendorStats(
    var id: String = "-1",
    var total_amount: String = "0",
    var total_orders: String = "0",
    var month: String = ""
) {
}