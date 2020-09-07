package com.anvesh.nogozoshopapplication.datamodels

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class VendorProfile(
    var name: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var profilelevel: String? = "",
    var cityname: String? = "",
    var cityid: String? = "",
    var areaname: String? = "",
    var areaid: String? = "",
    var address: String? = "",
    var homebusiness: String? = "",
    var deliverystatus: String? = "Not delivering",
    var shopname: String? = "",
    var deliverycharges: String? = "",
    var deliveryminorder: String? = ""
){

    constructor(email: String, profilelevel: String) : this() {
        this.email = email
        this.profilelevel = profilelevel
    }
}