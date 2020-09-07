package com.anvesh.nogozoshopapplication.ui.auth.vendor

import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.datamodels.CustomerProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DatabaseReference
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR

class VendorAuthFragmentViewModel
//@Inject constructor(
    //private val sessionManager: SessionManager)
:ViewModel() {

    private val sessionManager: SessionManager = SessionManager()

    fun login(email: String, password: String): Task<AuthResult> {
        return sessionManager.login(email, password)
    }

    fun getUserProfile(): DatabaseReference {
        return sessionManager.getUserProfile()
    }

    fun getNewUserProfile(): DatabaseReference {
        return sessionManager.getNewUserProfile()
    }

    fun saveProfileToLocal(profile: CustomerProfile){
        sessionManager.saveProfileToLocal(profile)
    }

    fun saveOnLogged(email: String){
        sessionManager.saveOnLogged(email, userType_VENDOR)
    }

    fun uploadToken(token: String){
        sessionManager.uploadToken(token)
    }

    fun register(email: String, password: String): Task<AuthResult> {
        return sessionManager.register(email, password)
    }

    fun saveOnRegistered(email: String) {
        sessionManager.saveOnRegistered(email, userType_VENDOR)
    }
}