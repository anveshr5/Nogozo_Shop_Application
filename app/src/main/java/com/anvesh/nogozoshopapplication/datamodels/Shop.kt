package com.anvesh.nogozoshopapplication.datamodels

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Shop {
    lateinit var shopName: String

    lateinit var shopId: String

    var imageUrl: String? = null

    var shopAddress: String? = null

    var shopCurrentStatus: String? = null

    var shopAreaId: String? = "-1"

    constructor(shopName: String, shopId: String, imageUrl: String?,shopAddress: String?, shopCurrentStatus: String?, shopAreaId: String?){
        this.shopId = shopId
        this.shopName = shopName
        this.imageUrl = imageUrl
        this.shopAddress = shopAddress
        this.shopCurrentStatus = shopCurrentStatus
        this.shopAreaId = shopAreaId
    }

    constructor()
}