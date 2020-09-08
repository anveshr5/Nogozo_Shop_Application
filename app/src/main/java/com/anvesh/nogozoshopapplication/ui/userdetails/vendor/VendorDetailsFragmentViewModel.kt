package com.anvesh.nogozoshopapplication.ui.userdetails.vendor

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.datamodels.NewShop
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class VendorDetailsFragmentViewModel
//  @Inject
//  constructor()
    : ViewModel() {

    fun setUserProfile(phone: String,newShop: NewShop ) {
        FirebaseDatabase.getInstance().reference.child("users").child("newshop").setValue(newShop)
    }

}
    //private val sessionManager: SessionManager = SessionManager()

    //private val userProfileStatus: MediatorLiveData<String> = MediatorLiveData()
    //private var userProfile: MediatorLiveData<DataResource<CustomerProfile>> = MediatorLiveData()

    //fun setUserProfile(map: HashMap<String, Any>): Task<Void> {
    //    return Database().setUserProfile("newshop", map)
    //}

    //fun saveProfileToLocal(map: HashMap<String, Any>) {
    //    sessionManager.saveCustomerProfileToLocal(map)
    //}

    //fun getUserProfileLevel(shopname: String) {
    //    val current_uid = FirebaseAuth.getInstance().currentUser!!.uid
    //    val profile_level_ref = FirebaseDatabase.getInstance()
    //        .getReference("/users/newshop/${shopname}/profile/profilelevel")

    //    profile_level_ref.addValueEventListener(object : ValueEventListener {
    //        override fun onDataChange(snapshot: DataSnapshot) {
    //            userProfileStatus.value = snapshot.value as String
    //        }

    //        override fun onCancelled(error: DatabaseError) {
    //        }

    //    })
    //}

    //fun getProfileStatusLiveData(): LiveData<String> {
    //    return userProfileStatus
    //}

    /*fun getnewshopDetailsLiveData() {
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
    }*/