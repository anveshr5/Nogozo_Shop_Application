package com.anvesh.nogozoshopapplication.ui.inventory

import com.anvesh.nogozoshopapplication.datamodels.Item


interface InventoryCommunicator {
    fun onItemClick(item: Item)

    fun onNewItem()

    fun onBackPressedFromEdit()
}