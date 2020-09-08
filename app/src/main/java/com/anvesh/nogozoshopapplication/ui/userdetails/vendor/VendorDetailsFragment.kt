package com.anvesh.nogozoshopapplication.ui.userdetails.vendor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.CustomerProfile
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.main.DataResource
import com.anvesh.nogozoshopapplication.util.Constants.PROFILE_LEVEL_0
import com.anvesh.nogozoshopapplication.util.Constants.PROFILE_LEVEL_1
import com.anvesh.nogozoshopapplication.util.Helper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class VendorDetailsFragment : BaseFragment(R.layout.fragment_userdetails_vendor),
    View.OnClickListener {

    private lateinit var viewModel: VendorDetailsFragmentViewModel

    lateinit var shopownernameField: TextInputEditText
    lateinit var shopnameField: TextInputEditText
    lateinit var phoneField: TextInputEditText
    private lateinit var confirmButton: MaterialButton
    private lateinit var progressBar: ProgressBar


    private var oldProfile: CustomerProfile? = null
    private var newProfile: CustomerProfile? = null

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

        checkProfileLevel()
    }

    private fun checkProfileLevel() {
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
    }

    private fun getNewShopDetails() {
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
    }

    private fun setDatatoViews(data: CustomerProfile) {
        shopownernameField.setText(data.name)
        shopnameField.setText(data.shopname)
        phoneField.setText(data.phone)
    }

    private fun updateUserProfile() {

        val shopownername = shopownernameField.text.toString()
        val shopname = shopnameField.text.toString()
        val phone = phoneField.text.toString()

        val map: HashMap<String, Any> = HashMap()
        map["name"] = shopownername
        map["phone"] = phone
        map["shopname"] = shopname
        //map["cityname"] = selectedCity!!.cityName
        //map["cityid"] = selectedCity!!.cityId
        //map["areaname"] = selectedArea!!.areaName
        //map["areaid"] = selectedArea!!.areaId
        //map["address"] = address
        map["profilelevel"] = PROFILE_LEVEL_1
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
        map["date"] = dateFormatter.format(Date())

        viewModel.setUserProfile(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(activity as Context, "Profile details Updated", Toast.LENGTH_SHORT).show()
                //viewModel.saveProfileToLocal(map)
            } else if (!it.isSuccessful){
                Toast.makeText(activity as Context, "Some error occurred try again later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.vendor_userdetails_confirm_button -> {
                progressBar.visibility = View.VISIBLE
                if (shopownernameField.text!!.isNotEmpty()) {
                    if (shopnameField.text!!.isNotEmpty()) {
                        if (phoneField.text!!.toString().length == 10 || phoneField.text.toString().contentEquals("1234567890")) {
                            updateUserProfile()
                        } else {
                            showError("valid mobile number")
                        }
                    } else {
                        showError("your shop name")
                    }
                } else {
                    showError("your name")
                }
                progressBar.visibility =View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(activity as Context, "Please enter $message", Toast.LENGTH_SHORT).show()
    }
}