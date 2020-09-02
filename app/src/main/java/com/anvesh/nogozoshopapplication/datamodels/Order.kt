package com.anvesh.nogozoshopapplication.datamodels

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Order(
    var orderkey:String = "",
    var orderId: String = "",
    var items: HashMap<String, Any> = HashMap(),
    var date: String = "-1",
    var time: String = "-1",
    var datetime: String = "-1",
    var status: String = "-1",
    var price: String = "0",
    var itemprice: String = "",
    var shopid: String = "-1",
    var shopname: String = "",
    var shopinstruction: String = "",
    var delivery: String = "No",
    var deliverycharges: String = "0",
    var customername: String = "-1",
    var customeraddress: String = "-1",
    var customerphone: String = "-1"
){
    //constructor() : this() {}
}