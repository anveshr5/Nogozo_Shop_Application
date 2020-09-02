package com.anvesh.nogozoshopapplication.ui.splash

import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.ui.auth.AuthResource

class SplashActivityViewModel
    //@Inject
    //constructor(
      //  private val sessionManager: SessionManager)
    : ViewModel() {

    private val sessionManager: SessionManager = SessionManager()

    suspend fun getCurrentUser(): AuthResource {
        return sessionManager.getCurrentUser()
    }

    suspend fun getUserType(): String {
        return sessionManager.getUserType()
    }

    suspend fun getProfileLevel(): String {
        return sessionManager.getProfileLevel()
    }
}