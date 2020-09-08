package com.anvesh.nogozoshopapplication.ui.userdetails.vendor

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.NewShop
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase

class VendorDetailsFragment : BaseFragment(R.layout.fragment_userdetails_vendor),
    View.OnClickListener {

    private lateinit var viewModel: VendorDetailsFragmentViewModel

    lateinit var shopownernameField: TextInputEditText
    lateinit var shopnameField: TextInputEditText
    lateinit var phoneField: TextInputEditText
    private lateinit var confirmButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var newShop= NewShop("","","")

    //private var oldProfile: CustomerProfile? = null
    //private var newProfile: CustomerProfile? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[VendorDetailsFragmentViewModel::class.java]

        shopownernameField = view.findViewById(R.id.vendor_userdetails_name_field)
        shopnameField = view.findViewById(R.id.vendor_userdetails_shop_name_field)
        phoneField = view.findViewById(R.id.vendor_userdetails_phone_field)
        confirmButton = view.findViewById(R.id.vendor_userdetails_confirm_button)
        confirmButton.setOnClickListener(this)
        progressBar = view.findViewById(R.id.vendor_profile_progressbar)

        progressBar.visibility = View.GONE
    }

    private fun updateUserProfile() {
        progressBar.visibility = View.VISIBLE
        val shopownername = shopownernameField.text.toString()
        val shopname = shopnameField.text.toString()
        val phone = phoneField.text.toString()

        newShop = NewShop(shopownername, shopname, phone)

        FirebaseDatabase.getInstance().getReference("newshop").push().setValue(newShop)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        activity as Context,
                        "We'll Contact you soon!",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility =View.GONE
                    //viewModel.saveProfileToLocal(map)
                } else if (!it.isSuccessful) {
                    Toast.makeText(
                        activity as Context,
                        "Some error occurred try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility =View.GONE
                }
            }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.vendor_userdetails_confirm_button -> {
                progressBar.visibility = View.VISIBLE
                if (shopownernameField.text!!.isNotEmpty()) {
                    val shopownername = shopownernameField.text.toString()
                    if (shopnameField.text!!.isNotEmpty()) {
                        val shopname = shopnameField.text.toString()
                        if (phoneField.text!!.toString().length == 10 || phoneField.text.toString()
                                .contentEquals("1234567890")
                        ) {
                            val phone = phoneField.text.toString()
                            if (shopownername != newShop.name && shopname != newShop.shopname && phone != newShop.phone)
                                updateUserProfile()
                            else
                                Toast.makeText(activity as Context, "You have been registered, We'll contact you soon!", Toast.LENGTH_SHORT).show()
                        } else {
                            showError("valid mobile number")
                        }
                    } else {
                        showError("your shop name")
                    }
                } else {
                    showError("your name")
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(activity as Context, "Please enter $message", Toast.LENGTH_SHORT).show()
    }
}
/*    private fun checkProfileLevel() {
        viewModel.getProfileStatusLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getProfileStatusLiveData().observe(viewLifecycleOwner, Observer {
            if (it == PROFILE_LEVEL_1) {
                getNewShopDetails()
            } else if(it == PROFILE_LEVEL_0) {
                progressBar.visibility = View.GONE
                confirmButton.visibility = View.VISIBLE
            }
        })
        viewModel.getUserProfileLevel()
    }                                                                                             */

/*    private fun getNewShopDetails() {
        viewModel.getProfileLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getProfileLiveData().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                DataResource.Status.SUCCESS -> {
                    setDatatoViews(it.data)
                    newProfile = CustomerProfile()

                    newProfile!!.name = it.data.name
                    newProfile!!.phone = it.data.phone
                    newProfile!!.cityid = it.data.cityid
                    newProfile!!.cityname = it.data.cityname
                    newProfile!!.areaname = it.data.areaname
                    newProfile!!.areaid = it.data.areaid
                    newProfile!!.email = it.data.email
                    newProfile!!.address = it.data.address
                    newProfile!!.profilelevel = it.data.profilelevel

                    oldProfile = it.data
                    confirmButton.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
                DataResource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    confirmButton.visibility = View.INVISIBLE
                }
                DataResource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    confirmButton.visibility = View.INVISIBLE
                }
            }
        })

        viewModel.getnewshopDetailsLiveData()
    }                                                                                             */

//    private fun setDatatoViews(data: CustomerProfile) {
//        shopownernameField.setText(data.name)
//        shopnameField.setText(data.shopname)
//        phoneField.setText(data.phone)
//    }