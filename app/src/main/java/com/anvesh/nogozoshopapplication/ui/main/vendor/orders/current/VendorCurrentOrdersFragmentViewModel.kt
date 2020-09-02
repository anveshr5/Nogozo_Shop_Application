package com.anvesh.nogozoshopapplication.ui.main.vendor.orders.current

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.datamodels.Order
import com.anvesh.nogozoshopapplication.network.Database
import com.anvesh.nogozoshopapplication.ui.main.DataResource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class VendorCurrentOrdersFragmentViewModel
//    @Inject
//  constructor(
//    private val sessionManager: SessionManager,
//  private val database: Database)
    : ViewModel() {

    private val sessionManager: SessionManager = SessionManager()
    private val database: Database = Database()

    private val currentOrders: MediatorLiveData<DataResource<ArrayList<Order>>> = MediatorLiveData()
    private val temparrayList: ArrayList<Order> = ArrayList()

    fun getCurrentOrders() {
        if (currentOrders.value != null) {
            if (currentOrders.value!!.status == DataResource.Status.LOADING)
                return
        }
        currentOrders.value = DataResource.loading()
        database.getCurrentOrders(sessionManager.getUserType(), sessionManager.getUserId())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    currentOrders.value = DataResource.error(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val orderIds = snapshot.value as HashMap<String, String>
                        if (orderIds.size == 0) {
                            currentOrders.value = DataResource.error("No Orders")
                        } else {
                            getOrderDetails(orderIds.keys)
                        }
                    } else {
                        currentOrders.value = DataResource.error("No Orders")
                    }
                }
            })
    }

    fun getLiveData(): LiveData<DataResource<ArrayList<Order>>> {
        return currentOrders
    }

    private fun getOrderDetails(orderId: Set<String>) {
        for (key in orderId) {
            database.getOrderDetails(key)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val order = snapshot.getValue<Order>()!!
                        order.orderkey = snapshot.key!!.toString()
                        temparrayList.add(order)

                        if (temparrayList.size == orderId.size) {
                            currentOrders.value =
                                DataResource.success(temparrayList.clone() as ArrayList<Order>)
                            temparrayList.clear()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }
}