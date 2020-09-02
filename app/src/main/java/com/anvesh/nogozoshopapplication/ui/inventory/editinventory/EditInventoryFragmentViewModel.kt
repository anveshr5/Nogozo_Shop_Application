package com.anvesh.nogozoshopapplication.ui.inventory.editinventory

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.network.Database
import com.google.android.gms.tasks.Task

class EditInventoryFragmentViewModel
    //@Inject
    //constructor(
//        private val sessionManager: SessionManager,
  //      private val database: Database)
: ViewModel() {

    private val sessionManager: SessionManager = SessionManager()
    private val database: Database = Database()

    fun updateItem(itemId: String, map: HashMap<String, Any>, imageUri: Uri? = null): Task<Void>{
        return database.editItemInShop(sessionManager.getUserId(),itemId, map, imageUri)
    }

    fun createItem(map: HashMap<String, Any>, imageUri: Uri? = null): Task<Void> {
        return database.createItemInShop(sessionManager.getUserId(), map, imageUri)
    }
}