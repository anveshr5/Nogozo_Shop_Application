package com.anvesh.nogozoshopapplication.ui.auth.vendor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.CustomerProfile
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.main.MainActivity
import com.anvesh.nogozoshopapplication.ui.userdetails.UserDetailsActivity
import com.anvesh.nogozoshopapplication.util.Constants
import com.anvesh.nogozoshopapplication.util.Constants.USER_TYPE
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR
import com.anvesh.nogozoshopapplication.util.Helper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VendorAuthFragment : BaseFragment(R.layout.fragment_auth_vendor_signin),
    View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: VendorAuthFragmentViewModel

    private val viewType_SIGNUP = 0
    private val viewType_SIGNIN = 1
    private var viewType = viewType_SIGNIN

    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var signinButton: MaterialButton
    private lateinit var signupButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var cc1: ConstraintLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emailField = view.findViewById(R.id.vendor_email_field)
        passwordField = view.findViewById(R.id.vendor_password_field)
        progressBar = view.findViewById(R.id.auth_vendor_progressBar)
        signinButton = view.findViewById(R.id.vendor_login_button)
        signinButton.setBackgroundColor(
            resources.getColor(
                R.color.colorPrimary,
                resources.newTheme()
            )
        )
        signinButton.setOnClickListener(this)
        signupButton = view.findViewById(R.id.vendor_signup_button)
        signupButton.setOnClickListener(this)
        cc1 = view.findViewById(R.id.vendor_auth_signin)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[VendorAuthFragmentViewModel::class.java]
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.vendor_login_button -> {
                if (viewType == viewType_SIGNIN)
                    login()
                else if (viewType == viewType_SIGNUP)
                    animate()
            }
            R.id.vendor_signup_button -> {
                if (viewType == viewType_SIGNUP) {
                    register()
                } else if (viewType == viewType_SIGNIN) {
                    animate()
                }
            }
        }
    }

    private fun login() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            if (Helper.isValidEmail(email)) {
                if (password.length > 6) {
                    val task =
                        viewModel.login(emailField.text.toString(), passwordField.text.toString())
                    task.addOnCompleteListener {
                        if (it.isSuccessful) {
                            viewModel.saveOnLogged(email)
                            viewModel.getUserProfile()
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {
                                        showToast(error.message)
                                        FirebaseAuth.getInstance().signOut()
                                        showToast("Something Went Wrong")
                                        progressBar.visibility = View.GONE
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val vendorProfile = snapshot.getValue<CustomerProfile>()
                                        if (vendorProfile != null) {
                                            if (vendorProfile.profilelevel == Constants.PROFILE_LEVEL_2) {
                                                viewModel.saveProfileToLocal(vendorProfile)
                                                val i = Intent(context, MainActivity::class.java)
                                                i.putExtra(USER_TYPE, userType_VENDOR)
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                startActivity(i)
                                            }
                                            uploadToken()
                                        } else {
                                            checkNewShops(email)
                                        }
                                    }
                                })
                        } else {
                            showToast(it.exception!!.message!!)
                            progressBar.visibility = View.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Please Enter at least 6 digit password")
                        progressBar.visibility = View.GONE
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Please Enter Valid Email")
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    fun checkNewShops(email: String){
        viewModel.saveOnLogged(email)
        viewModel.getNewUserProfile().addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val vendorProfile = snapshot.getValue<CustomerProfile>()
                if (vendorProfile != null) {
                    if (vendorProfile.profilelevel == Constants.PROFILE_LEVEL_0) {
                        val i = Intent(context, UserDetailsActivity::class.java)
                        i.putExtra(USER_TYPE, userType_VENDOR)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(i)
                    } else if (vendorProfile.profilelevel == Constants.PROFILE_LEVEL_1) {
                        val i = Intent(context, UserDetailsActivity::class.java)
                        i.putExtra(USER_TYPE, userType_VENDOR)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(i)
                    } else {
                        progressBar.visibility = View.GONE
                        FirebaseAuth.getInstance().signOut()
                        showToast("Looks Like you are not Registered.\nContact Our Support Team to register.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast(error.message)
                FirebaseAuth.getInstance().signOut()
                showToast("Something Went Wrong")
                progressBar.visibility = View.GONE            }
        })
    }

    private fun register() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            if (Helper.isValidEmail(email)) {
                if (password.length >= 6) {        //change
                    val task = viewModel.register(email, password)
                    task.addOnCompleteListener {
                        if (task.isSuccessful) {
                            uploadToken()
                            viewModel.saveOnRegistered(email)
                            val i = Intent(context, UserDetailsActivity::class.java)
                            i.putExtra(USER_TYPE, userType_VENDOR)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(i)
                        } else {
                            showToast(it.exception!!.message!!)
                            progressBar.visibility = View.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Please Enter at least 6 digit password")
                        progressBar.visibility = View.GONE
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    showToast("Please Enter Valid Email")
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun animate() {

        viewType = if (viewType == viewType_SIGNIN) viewType_SIGNUP else viewType_SIGNIN

        val constraintSet1 = ConstraintSet()
        constraintSet1.clone(context, R.layout.fragment_auth_vendor_signin)

        val constraintSet2 = ConstraintSet()
        constraintSet2.clone(context, R.layout.fragment_auth_vendor_signup)

        TransitionManager.beginDelayedTransition(cc1)
        val constraint =
            if (viewType == viewType_SIGNIN) {
                signupButton.text = "New to Nogozo?\nRegister as Merchant!"
                signupButton.setTextColor(
                    resources.getColor(
                        R.color.colorPrimary,
                        resources.newTheme()
                    )
                )
                signupButton.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        resources.newTheme()
                    )
                )
                signinButton.text = "Login"
                signinButton.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                signinButton.setBackgroundColor(
                    resources.getColor(
                        R.color.colorPrimary,
                        resources.newTheme()
                    )
                )
                constraintSet1
            } else {
                signupButton.text = "Register"
                signupButton.setTextColor(resources.getColor(R.color.white, resources.newTheme()))
                signupButton.setBackgroundColor(
                    resources.getColor(
                        R.color.colorPrimary,
                        resources.newTheme()
                    )
                )
                signinButton.text = "Already Have Account? Login"
                signinButton.setTextColor(
                    resources.getColor(
                        R.color.colorAccent,
                        resources.newTheme()
                    )
                )
                signinButton.setBackgroundColor(
                    resources.getColor(
                        R.color.white,
                        resources.newTheme()
                    )
                )
                constraintSet2
            }
        constraint.applyTo(cc1)

    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun uploadToken() {
        val sp = context!!.getSharedPreferences("notification", Context.MODE_PRIVATE)
        if (sp.contains("token"))
            viewModel.uploadToken(sp.getString("token", "")!!)
    }
}