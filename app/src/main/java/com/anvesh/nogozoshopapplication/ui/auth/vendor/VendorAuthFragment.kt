package com.anvesh.nogozoshopapplication.ui.auth.vendor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.CustomerProfile
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.ViewModelFactory
import com.anvesh.nogozoshopapplication.ui.main.MainActivity
import com.anvesh.nogozoshopapplication.ui.userdetails.UserDetailsActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.anvesh.nogozoshopapplication.util.Constants
import com.anvesh.nogozoshopapplication.util.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VendorAuthFragment: BaseFragment(R.layout.fragment_auth_vendor_signin),View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: VendorAuthFragmentViewModel

    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var progressBar: ProgressBar


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        emailField = view.findViewById(R.id.vendor_email_field)
        passwordField = view.findViewById(R.id.vendor_password_field)
        progressBar = view.findViewById(R.id.auth_vendor_progressBar)
        loginButton = view.findViewById(R.id.vendor_login_button)
        loginButton.setOnClickListener(this)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[VendorAuthFragmentViewModel::class.java]
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.vendor_login_button -> {
                login()
            }
        }
    }

    private fun login(){
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch{
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            if(Helper.isValidEmail(email)){
                if(password.length > 6){
                    val task = viewModel.login(emailField.text.toString(), passwordField.text.toString())
                    task.addOnCompleteListener{
                        if(it.isSuccessful){
                            viewModel.saveOnLogged(email)
                            viewModel.getUserProfile()
                                .addListenerForSingleValueEvent(object: ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {
                                        showToast(error.message)
                                        FirebaseAuth.getInstance().signOut()
                                        showToast("Something Went Wrong")
                                        progressBar.visibility = View.GONE
                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val vendorProfile = snapshot.getValue<CustomerProfile>()
                                        if(vendorProfile != null){
                                            if(vendorProfile.profilelevel == Constants.PROFILE_LEVEL_0){
                                                val i = Intent(context, UserDetailsActivity::class.java)
                                                i.putExtra(Constants.USER_TYPE, Constants.userType_VENDOR)
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                startActivity(i)
                                            }
                                            else if(vendorProfile.profilelevel == Constants.PROFILE_LEVEL_1){
                                                viewModel.saveProfileToLocal(vendorProfile)
                                                val i = Intent(context, MainActivity::class.java)
                                                i.putExtra(Constants.USER_TYPE, Constants.userType_VENDOR)
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                startActivity(i)
                                            }
                                            uploadToken()
                                        }else{
                                            progressBar.visibility = View.GONE
                                            FirebaseAuth.getInstance().signOut()
                                            showToast("Looks Like you are not Registered.\nContact Our Support Team to register.")
                                        }
                                    }
                                })
                        }else{
                            showToast(it.exception!!.message!!)
                            progressBar.visibility = View.GONE
                        }
                    }
                }else{
                    withContext(Dispatchers.Main){
                        showToast("Please Enter at least 6 digit password")
                        progressBar.visibility = View.GONE
                    }
                }
            }else{
                withContext(Dispatchers.Main){
                    showToast("Please Enter Valid Email")
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showToast(msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun uploadToken(){
        val sp = context!!.getSharedPreferences("notification", Context.MODE_PRIVATE)
        if(sp.contains("token"))
            viewModel.uploadToken(sp.getString("token","")!!)
    }
}