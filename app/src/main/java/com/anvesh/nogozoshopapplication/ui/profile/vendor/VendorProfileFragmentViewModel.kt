package com.anvesh.nogozoshopapplication.ui.profile.vendor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.datamodels.VendorProfile
import com.anvesh.nogozoshopapplication.network.Database
import com.anvesh.nogozoshopapplication.ui.main.DataResource
import com.anvesh.nogozoshopapplication.util.Constants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class VendorProfileFragmentViewModel
//@Inject
//constructor(
// private val sessionManager: SessionManager,
//private val database: Database )
    : ViewModel() {

    private val sessionManager: SessionManager = SessionManager()
    private val database: Database = Database()

    private var userProfile: MediatorLiveData<DataResource<VendorProfile>> = MediatorLiveData()

    fun getUserProfile() {
        if (userProfile.value != null) {
            if (userProfile.value!!.status == DataResource.Status.LOADING)
                return
        }

        userProfile.value = DataResource.loading()
        database.getUserProfile(Constants.userType_VENDOR)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    userProfile.value = DataResource.error(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val profile = snapshot.getValue<VendorProfile>()
                        userProfile.value = DataResource.success(profile!!)
                    } else {
                        userProfile.value = DataResource.error("Something Went Wrong")
                    }
                }
            })
    }

    fun getLiveData(): LiveData<DataResource<VendorProfile>> {
        return userProfile
    }
}