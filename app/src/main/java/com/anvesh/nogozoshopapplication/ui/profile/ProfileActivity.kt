package com.anvesh.nogozoshopapplication.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.ui.BaseActivity
import com.anvesh.nogozoshopapplication.ui.profile.vendor.VendorProfileFragment
import com.anvesh.nogozoshopapplication.util.Constants.USER_TYPE
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR

class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userType = intent.getStringExtra(USER_TYPE)
        if(userType == userType_VENDOR)
            startFragment(VendorProfileFragment())
    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.profile_container, fragment)
        ft.addToBackStack(fragment.tag)
        ft.commit()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }
}