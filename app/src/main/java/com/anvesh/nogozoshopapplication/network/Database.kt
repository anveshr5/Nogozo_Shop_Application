package com.anvesh.nogozoshopapplication.network

import android.net.Uri
import com.anvesh.nogozoshopapplication.datamodels.CustomerProfile
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage

class Database {


    fun uploadToken(token: String) {
        FirebaseDatabase.getInstance().reference.child("token")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .setValue(token)
    }

    fun getUserProfile(userType: String): DatabaseReference {

        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile")
    }

    fun setUserProfile(userType: String, map: HashMap<String, Any>): Task<Void> {
        val ref = FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile")

        return ref.updateChildren(map)
    }

    fun setUserProfileOnRegistered(userType: String, profile: CustomerProfile) {
        val ref = FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("profile")

        val map: HashMap<String, Any> = HashMap()
        map["email"] = profile.email!!
        map["profilelevel"] = profile.profilelevel!!

        ref.updateChildren(map)
    }

    fun getCities(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("citylist")
    }

    fun getAreas(cityId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("arealist").child(cityId)
    }

    fun getServices(): Query {
        return FirebaseDatabase.getInstance().reference
            .child("service").orderByChild("priority")
    }

    fun getShops(serviceId: String, cityId: String): Query {
        return FirebaseDatabase.getInstance().reference
            .child("shops").child(cityId)
            .orderByChild("serviceid/$serviceId").equalTo(true)
    }

    fun getItems(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("items").child(shopId)
    }

    fun getItemById(shopId: String, itemId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("items").child(shopId).child(itemId)
    }

    fun changeItemAvailabilityStatus(
        shopId: String,
        itemId: String,
        itemAvailabilityStatus: String
    ) {
        FirebaseDatabase.getInstance().reference
            .child("items").child(shopId).child(itemId)
            .child("isAvailable").setValue(itemAvailabilityStatus)
    }

    fun getShopAddress(shopid: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopid).child("address")
    }

    fun getShopAreaId(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopId).child("profile").child("areaid")
    }

    fun getShopStatus(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopId).child("status")
    }

    fun getDeliveryStatus(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopId).child("deliverystatus")
    }

    fun getFare(
        price: String,
        cityId: String,
        areaId: String,
        shopAreaId: String
    ): Task<HttpsCallableResult> {
        val data: HashMap<String, String> = HashMap()
        data["itemprice"] = price
        data["areaid"] = areaId
        data["cityid"] = cityId
        data["shopareaid"] = shopAreaId

        return FirebaseFunctions.getInstance()
            .getHttpsCallable("fare")
            .call(data)
    }

    fun getCurrentOrders(userType: String, userId: String): Query {
        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(userId).child("orders").orderByValue().equalTo("current")
    }

    fun getPastOrder(userType: String, userId: String): Query {
        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType)
            .child(userId).child("orders").orderByValue().equalTo("history")
    }

    fun getOrderDetails(orderId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("orders").child(orderId)
    }

    fun changeShopStatus(shopId: String, status: String): Task<Void> {
        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType_VENDOR)
            .child(shopId).child("status")
            .setValue(status)
    }

    fun changeDeliveryStatus(shopId: String, status: String): Task<Void> {
        return FirebaseDatabase.getInstance().reference
            .child("users").child(userType_VENDOR)
            .child(shopId).child("deliverystatus")
            .setValue(status)
    }

    fun getShopStats(shopId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
            .child("users").child("shop")
            .child(shopId).child("stats")
    }

    fun createItemInShop(
        shopId: String,
        map: HashMap<String, Any>,
        imagePath: Uri? = null
    ): Task<Void> {
        val itemid = FirebaseDatabase.getInstance().reference
            .child("items").child(shopId).push().key!!

        if (imagePath != null) {
            FirebaseStorage.getInstance().reference
                .child("items").child(itemid).putFile(imagePath)
        }

        return FirebaseDatabase.getInstance().reference
            .child("items").child(shopId).child(itemid).setValue(map)
    }

    fun editItemInShop(
        shopId: String,
        itemId: String,
        map: HashMap<String, Any>,
        imagePath: Uri? = null
    ): Task<Void> {
        val ref = FirebaseDatabase.getInstance().reference
            .child("items").child(shopId).child(itemId)

        if (imagePath != null) {
            FirebaseStorage.getInstance().reference
                .child("items").child(itemId).putFile(imagePath)
        }

        return ref.updateChildren(map)
    }

    fun markedOrderPacked(orderId: String, status: String): Task<Void> {
        return FirebaseDatabase.getInstance().reference
            .child("orders").child(orderId)
            .child("status").setValue(status)
    }

    fun searchItemsinCity(query: String, cityId: String): Task<HttpsCallableResult> {
        val data: HashMap<String, String> = HashMap()
        data["query"] = query
        data["cityid"] = cityId
        return FirebaseFunctions.getInstance()
            .getHttpsCallable("search")
            .call(data)
    }
}