package com.anvesh.nogozoshopapplication.ui.userdetails.vendor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.datamodels.CustomerProfile
import com.anvesh.nogozoshopapplication.network.Database
import com.anvesh.nogozoshopapplication.ui.main.DataResource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class VendorDetailsFragmentViewModel
//  @Inject
//  constructor()
    : ViewModel() {

    private val sessionManager: SessionManager = SessionManager()

    private val userProfileStatus: MediatorLiveData<String> = MediatorLiveData()
    private var userProfile: MediatorLiveData<DataResource<CustomerProfile>> = MediatorLiveData()


    fun setUserProfile(map: HashMap<String, Any>): Task<Void> {
        return Database().setUserProfile("newshop", map)
    }

    fun saveProfileToLocal(map: HashMap<String, Any>) {
        sessionManager.saveCustomerProfileToLocal(map)
    }

    fun getUserProfileLevel() {
        val current_uid = FirebaseAuth.getInstance().currentUser!!.uid
        val profile_level_ref = FirebaseDatabase.getInstance()
            .getReference("/users/newshop/${current_uid}/profile/profilelevel")

        profile_level_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userProfileStatus.value = snapshot.value as String
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun getProfileStatusLiveData(): LiveData<String> {
        return userProfileStatus
    }

    fun getnewshopDetailsLiveData() {
        userProfile.value = DataResource.loading()
        Database().getNewUserProfile()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val profile = snapshot.getValue<CustomerProfile>()
                        userProfile.value = DataResource.success(profile!!)
                    } else {
                        userProfile.value = DataResource.error("Something Went Wrong")
                    }
                }
            })
    }

    fun getProfileLiveData(): LiveData<DataResource<CustomerProfile>> {
        return userProfile
    }
}