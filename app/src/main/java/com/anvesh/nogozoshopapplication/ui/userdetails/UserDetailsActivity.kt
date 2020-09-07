package com.anvesh.nogozoshopapplication.ui.userdetails

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.ui.BaseActivity
import com.anvesh.nogozoshopapplication.ui.splash.SplashActivity
import com.anvesh.nogozoshopapplication.ui.userdetails.vendor.VendorDetailsFragment
import com.anvesh.nogozoshopapplication.util.Constants.USER_TYPE
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class UserDetailsActivity : BaseActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val i = intent
        if(i.getStringExtra(USER_TYPE) == userType_VENDOR)
            startFragment(VendorDetailsFragment())
    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.userdetails_container, fragment)
        ft.commit()
    }

    override fun onBackPressed() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        //finish()
    }
}