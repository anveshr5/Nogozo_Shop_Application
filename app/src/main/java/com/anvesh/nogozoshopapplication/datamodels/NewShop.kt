package com.anvesh.nogozoshopapplication.datamodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class NewShop(val name: String,val shopname: String, val phone: String):Parcelable {
    constructor(): this("","","")
}