package com.anvesh.nogozoshopapplication.ui.main.status

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.network.Database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class CurrentStatusFragmentViewModel: ViewModel() {

    val sessionManager = SessionManager()
    val database = Database()
    private val shopStatus: MediatorLiveData<String> = MediatorLiveData()
    private val deliveryStatus: MediatorLiveData<String> = MediatorLiveData()

    fun getCurrentDeliveryStatus(){
        database.getDeliveryStatus(sessionManager.getUserId()).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                deliveryStatus.value = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getDeliveryStatusLiveData(): LiveData<String> {
        return deliveryStatus
    }

    fun changeDeliveryStatus() {
        var status = deliveryStatus.value
        if(status == null)
            status = "Delivering"
        else{
            if(status == "Delivering")
                status = "Not Delivering"
            else if(status == "Not Delivering")
                status = "Delivering"
        }
        Log.d("vididid", status)
        database.changeDeliveryStatus(sessionManager.getUserId(), status)
    }

    fun getCurrentShopStatus(){
        database.getShopStatus(sessionManager.getUserId()).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                shopStatus.value = snapshot.value as String
            }
        })
    }

    fun getStatusLiveData(): LiveData<String>{
        return shopStatus
    }

    fun changeShopStatus() {
        var status = shopStatus.value
        if(status == null)
            status = "OPEN"
        else{
            if(status == "OPEN")
                status = "CLOSED"
            else if(status == "CLOSED")
                status = "OPEN"
        }
        database.changeShopStatus(sessionManager.getUserId(), status)
    }
}