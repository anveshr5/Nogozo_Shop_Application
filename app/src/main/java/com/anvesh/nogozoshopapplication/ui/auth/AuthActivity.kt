package com.anvesh.nogozoshopapplication.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.ui.BaseActivity
import com.anvesh.nogozoshopapplication.ui.auth.vendor.VendorAuthFragment
import com.anvesh.nogozoshopapplication.util.Constants.USER_TYPE
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR


class AuthActivity : BaseActivity() {

    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        userType = intent.getStringExtra(USER_TYPE)

        if(userType.equals(userType_VENDOR))
            startFragment(VendorAuthFragment())

    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.auth_container, fragment)
        ft.commit()
    }
}