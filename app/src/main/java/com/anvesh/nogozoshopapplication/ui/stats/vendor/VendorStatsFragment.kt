package com.anvesh.nogozoshopapplication.ui.stats.vendor

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.VendorStats
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.main.DataResource
import com.anvesh.nogozoshopapplication.ui.stats.StatsAdapter
import com.anvesh.nogozoshopapplication.util.Helper
import com.anvesh.nogozoshopapplication.util.VerticalSpacingItemDecoration

class VendorStatsFragment : BaseFragment(R.layout.fragment_stats_vendor) {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: VendorStatsFragmentViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatsAdapter

    lateinit var spinnerYear: Spinner
    lateinit var spinnerMonth: Spinner
    lateinit var getSelectedStats: Button
    private lateinit var month: TextView
    private lateinit var orders: TextView
    private lateinit var amount: TextView

    var keyMonth = ""
    var keyYear = ""
    var total_orders = 0
    var total_amount = 0

    private var vendorStatsArray = arrayListOf<VendorStats>()

    private var itemListMonths = arrayListOf<String>("Month")

    private var itemListYear = arrayListOf<String>("Year")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[VendorStatsFragmentViewModel::class.java]

        spinnerYear = view.findViewById(R.id.spinnerYear)
        spinnerMonth = view.findViewById(R.id.spinnerMonth)
        getSelectedStats = view.findViewById(R.id.getSelectedStats)
        month = view.findViewById(R.id.list_item_stats_month)
        orders = view.findViewById(R.id.list_item_stats_orders)
        amount = view.findViewById(R.id.list_item_stats_amount)
        recyclerView = view.findViewById(R.id.vendor_stats_recyclerview)

        initRecycler()
        subscribeObserver()
        viewModel.getStats()
        getSelectedStats.setOnClickListener {
            getSelectedStats()
        }
    }

    private fun getKeyYear() {
        val yearAdapter = ArrayAdapter(
            context!!.applicationContext,
            android.R.layout.simple_list_item_1,
            itemListYear
        )
        spinnerYear.adapter = yearAdapter
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                keyYear = parent?.getItemAtPosition(position) as String
                if (position != 0 && keyYear != "Overall") {
                    getMonthsOfYear()
                    getKeyMonth()
                } else if (keyYear == "Overall") {
                    displayOverallStats()
                    getSelectedStats.visibility = View.GONE
                    spinnerMonth.visibility = View.GONE
                    keyMonth = ""
                } else {
                    getSelectedStats.visibility = View.GONE
                    spinnerMonth.visibility = View.GONE
                    keyMonth = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun displayOverallStats() {
        month.text = "Your Overall Stats"
        orders.text = total_orders.toString()
        amount.text = "₹${total_amount}"
    }

    private fun calcOverAllStats() {
        vendorStatsArray.forEach {
            total_orders += it.total_orders.toInt()
            total_amount += it.total_amount.toInt()
        }
    }

    private fun getKeyMonth() {
        spinnerMonth.visibility = View.VISIBLE
        val monthAdapter = ArrayAdapter(
            context!!.applicationContext,
            android.R.layout.simple_list_item_1,
            itemListMonths
        )
        spinnerMonth.adapter = monthAdapter
        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val monthName = parent?.getItemAtPosition(position) as String
                keyMonth = Helper.getMonthNumber(monthName)
                if (position != 0) {
                    getSelectedStats.visibility = View.VISIBLE
                } else {
                    getSelectedStats.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    private fun getSelectedStats() {
        val key = keyYear + keyMonth
        var monthlyStats: VendorStats = VendorStats("196801", "-1", "-1", "Some Problem Occurred")
        vendorStatsArray.forEach {
            if (it.id == key) {
                Log.d("MonthlyStat", it.toString())
                monthlyStats = it
            }
        }

        month.text = monthlyStats.month + " - ${keyYear}"
        orders.text = monthlyStats.total_orders
        amount.text = "₹${monthlyStats.total_amount}"
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(16))
        adapter = StatsAdapter()
        recyclerView.adapter = adapter
    }

    private fun subscribeObserver() {
        viewModel.getStatsLiveData().removeObservers(viewLifecycleOwner)
        viewModel.getStatsLiveData().observe(viewLifecycleOwner, Observer { dataResource ->
            when (dataResource.status) {
                DataResource.Status.SUCCESS -> {
                    vendorStatsArray = dataResource.data
                    adapter.setData(dataResource.data)
                    setUpYearAndMonthList()
                    calcOverAllStats()
                }
                DataResource.Status.ERROR -> {
                    // show error
                }
                DataResource.Status.LOADING -> {
                    //show progressbar
                }
            }
        })
    }

    private fun setUpYearAndMonthList() {
        itemListYear.clear()
        itemListYear.add("Year")
        itemListYear.add("Overall")
        Log.d("Stats", "Requested")
        vendorStatsArray.forEach {
            if (!itemListYear.contains(it.id.take(4))) {
                itemListYear.add(it.id.take(4))
                getKeyYear()
            }
        }
    }

    private fun getMonthsOfYear() {
        itemListMonths.clear()
        itemListMonths.add("Month")
        vendorStatsArray.forEach {
            if (it.id.take(4) == keyYear) {
                itemListMonths.add(it.month)
            }
        }
        getKeyMonth()
    }
}