package com.anvesh.nogozoshopapplication.ui.main.status

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.ui.BaseFragment

class CurrentStatusFragment : BaseFragment(R.layout.fragment_current_status), View.OnClickListener {

    lateinit var sessionManager: SessionManager
    lateinit var viewModel: CurrentStatusFragmentViewModel

    //private lateinit var deliveryStatusTag: TextView
    //private lateinit var deliveryStatus: TextView
    //private lateinit var deliveryPower: ImageButton
    private lateinit var shopStatus: TextView
    private lateinit var power: ImageButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sessionManager = SessionManager()

        //deliveryStatusTag = view.findViewById(R.id.currentorder_vendor_delivery_header)
        //deliveryStatus = view.findViewById(R.id.currentorder_vendor_delivery_status)
        //deliveryPower = view.findViewById(R.id.currentorder_vendor_delivery_power)
        //deliveryPower.setOnClickListener(this)

        shopStatus = view.findViewById(R.id.currentorder_vendor_shop_status)
        power = view.findViewById(R.id.currentorder_vendor_power)
        power.setOnClickListener(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[CurrentStatusFragmentViewModel::class.java]

        getCurrentShopStatus()
        //getCurrentDeliveryStatus()

    }

/*    private fun getCurrentDeliveryStatus() {
        viewModel.getDeliveryStatusLiveData().removeObservers(viewLifecycleOwner)
        viewModel.getDeliveryStatusLiveData().observe(viewLifecycleOwner, Observer {
            if (it == "Delivering") {
                deliveryStatusTag.visibility = View.VISIBLE
                deliveryStatus.visibility = View.VISIBLE
                deliveryStatus.text = it
                deliveryStatus.setTextColor(resources.getColor(R.color.green, resources.newTheme()))
            } else if (it == "Not Delivering") {
                //deliveryStatus.text = it
                //deliveryStatus.setTextColor(resources.getColor(R.color.red, resources.newTheme()))
                deliveryStatus.visibility = View.GONE
                deliveryStatusTag.visibility = View.GONE
            }
        })
        viewModel.getCurrentDeliveryStatus()
    }
*/
    private fun getCurrentShopStatus() {
        viewModel.getStatusLiveData().removeObservers(viewLifecycleOwner)
        viewModel.getStatusLiveData().observe(viewLifecycleOwner, Observer {
            if (it == "OPEN") {
                shopStatus.text = it
                shopStatus.setTextColor(resources.getColor(R.color.green, resources.newTheme()))
            } else if (it == "CLOSED") {
                shopStatus.text = it
                shopStatus.setTextColor(resources.getColor(R.color.red, resources.newTheme()))
            }
        })
        viewModel.getCurrentShopStatus()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.currentorder_vendor_power -> {
                viewModel.changeShopStatus()
            }
            //R.id.currentorder_vendor_delivery_power -> {
            //    viewModel.changeDeliveryStatus()
            //}
        }
    }
}