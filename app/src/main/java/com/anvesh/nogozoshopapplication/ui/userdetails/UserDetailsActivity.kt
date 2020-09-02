package com.anvesh.nogozoshopapplication.ui.userdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.ui.BaseActivity
import com.anvesh.nogozoshopapplication.ui.userdetails.vendor.VendorDetailsFragment
import com.anvesh.nogozoshopapplication.util.Constants.USER_TYPE
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR
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
}