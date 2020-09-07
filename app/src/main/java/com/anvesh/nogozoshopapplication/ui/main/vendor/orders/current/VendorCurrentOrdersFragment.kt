package com.anvesh.nogozoshopapplication.ui.main.vendor.orders.current

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.main.DataResource
import com.anvesh.nogozoshopapplication.ui.main.vendor.orders.OrderAdapter
import com.anvesh.nogozoshopapplication.util.OrderByStatus
import com.anvesh.nogozoshopapplication.util.VerticalSpacingItemDecoration
import com.anvesh.nogozoshopapplication.util.Constants.userType_VENDOR

class VendorCurrentOrdersFragment(
    //private val communicator: Communicator
): BaseFragment(R.layout.fragment_main_currentorder_vendor),
    View.OnClickListener{

    //@Inject
    //lateinit var factory: ViewModelFactory
    //@Inject
    lateinit var sessionManager: SessionManager

    lateinit var viewModel: VendorCurrentOrdersFragmentViewModel
    private lateinit var adapter: OrderAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[VendorCurrentOrdersFragmentViewModel::class.java]

        sessionManager = SessionManager()
        recyclerView = view.findViewById(R.id.currentorder_vendor_recyclerview)
        progressBar = view.findViewById(R.id.fragment_currentorder_vendor_progressBar)
        swipeRefresh = view.findViewById(R.id.currentorder_vendor_swipeRefresh)

        initRecycler()

        swipeRefresh.setOnRefreshListener{
            swipeRefresh.isRefreshing = false
            viewModel.getCurrentOrders()
        }

        getCurrentOrders()
    }

    private fun initRecycler(){
        adapter = OrderAdapter(true, OrderByStatus())
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(16))
        recyclerView.adapter = adapter
    }

    private fun getCurrentOrders(){
        viewModel.getLiveData().removeObservers(viewLifecycleOwner)

        viewModel.getLiveData().observe(viewLifecycleOwner, Observer {
            when(it.status){
                DataResource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    adapter.setList(it.data)
                }
                DataResource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
                DataResource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })
        viewModel.getCurrentOrders()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
//            R.id.currentorder_vendor_power -> {
//                viewModel.changeShopStatus()
//            }
//            R.id.currentorder_vendor_delivery_power -> {
//                viewModel.changeDeliveryStatus()
//            }
        }
    }
}