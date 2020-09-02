package com.anvesh.nogozoshopapplication.ui.inventory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.Item
import com.anvesh.nogozoshopapplication.ui.BaseActivity
import com.anvesh.nogozoshopapplication.ui.inventory.editinventory.EditInventoryFragment
import com.anvesh.nogozoshopapplication.ui.inventory.inventory.VendorInventoryFragment

class VendorInventoryActivity: BaseActivity(), InventoryCommunicator{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        startFragment(VendorInventoryFragment(this))
    }

    private fun startFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.inventory_container, fragment)
        ft.addToBackStack(fragment.tag)
        ft.commit()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }

    override fun onNewItem(){
        startFragment(EditInventoryFragment(this))
    }

    override fun onItemClick(item: Item) {
        val f = EditInventoryFragment(this)
        val b = Bundle()
        b.putSerializable("item", item)
        f.arguments = b
        startFragment(f)
    }

    override fun onBackPressedFromEdit() {
        super.onBackPressed()
    }
}

