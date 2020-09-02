package com.anvesh.nogozoshopapplication.ui.inventory.inventory

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.Item
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.inventory.InventoryCommunicator
import com.anvesh.nogozoshopapplication.ui.main.DataResource
import com.anvesh.nogozoshopapplication.util.VerticalSpacingItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton

class VendorInventoryFragment(private val communicator: InventoryCommunicator) :
    BaseFragment(R.layout.fragment_inventory),
    InventoryAdapter.OnInventoryItemClick, View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: VendorInventoryFragmentViewModel
    private lateinit var adapter: InventoryAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var searchWrapper: CardView

    companion object{
        var search: String? = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[VendorInventoryFragmentViewModel::class.java]

        recyclerView = view.findViewById(R.id.inventory_recyclerview)
        fab = view.findViewById(R.id.inventory_fab)
        searchView = view.findViewById(R.id.inventory_searchview)
        searchWrapper = view.findViewById(R.id.inventory_search_wrapper)
        fab.setOnClickListener(this)
        progressBar = view.findViewById(R.id.inventory_progressbar)
        swipeRefresh = view.findViewById(R.id.inventory_swiperefresh)
        swipeRefresh.setOnRefreshListener {
            viewModel.getSearchedItems(search)
            swipeRefresh.isRefreshing = false
        }

        searchView.setIconifiedByDefault(false)

        //searchWrapper.setOnClickListener {
        //    searchView.requestFocus()
        //}

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String?): Boolean {
                search = newText?.trimEnd()
                viewModel.getSearchedItems(newText?.trimEnd())
                adapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                search = query?.trimEnd()
                viewModel.getSearchedItems(query?.trimEnd())
                adapter.notifyDataSetChanged()
                return true
            }
        })

        initRecycler()
        subscribeObserver()
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(VerticalSpacingItemDecoration(16))
        adapter = InventoryAdapter(this)
        recyclerView.adapter = adapter
    }

    private fun subscribeObserver() {
        viewModel.getLiveData().observe(this, Observer {
            when (it.status) {
                DataResource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                DataResource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                DataResource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    adapter.setDataList(it.data)
                }
            }
        })
        viewModel.getItems()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.inventory_fab -> {
                communicator.onNewItem()
            }
        }
    }

    override fun onInventoryItemClick(item: Item) {
        communicator.onItemClick(item)
    }

    override fun onAvailabililtyChanged(itemId: String, newAvailabilityStatus: String) {
        viewModel.changeItemStatus(itemId, newAvailabilityStatus)
    }
}