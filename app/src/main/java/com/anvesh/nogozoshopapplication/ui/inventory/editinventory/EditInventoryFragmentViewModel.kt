package com.anvesh.nogozoshopapplication.ui.inventory.editinventory

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.anvesh.nogozoshopapplication.SessionManager
import com.anvesh.nogozoshopapplication.datamodels.ItemGroup
import com.anvesh.nogozoshopapplication.network.Database
import com.anvesh.nogozoshopapplication.ui.userdetails.CityResource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.acos

class EditInventoryFragmentViewModel
//@Inject
//constructor(
//        private val sessionManager: SessionManager,
//      private val database: Database)
    : ViewModel() {

    private val sessionManager: SessionManager = SessionManager()
    private val database: Database = Database()

    var list: ArrayList<ItemGroup> = ArrayList()
    private var itemGroups: MediatorLiveData<CityResource<List<ItemGroup>>> = MediatorLiveData()

    fun updateItem(itemId: String, map: HashMap<String, Any>, imageUri: Uri? = null): Task<Void> {
        return database.editItemInShop(sessionManager.getUserId(), itemId, map, imageUri)
    }

    fun createItem(map: HashMap<String, Any>, imageUri: Uri? = null): Task<Void> {
        return database.createItemInShop(sessionManager.getUserId(), map, imageUri)
    }



    fun getItemGroups(): LiveData<CityResource<List<ItemGroup>>> {
        itemGroups.value = CityResource.loading()

        FirebaseDatabase.getInstance().getReference("itemgroups/${FirebaseAuth.getInstance().uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.Default).launch {
                        val list1: ArrayList<ItemGroup> = ArrayList()
                        if (snapshot.value != null) {
                            val map = snapshot.value as HashMap<String, String>
                            for ((key, value) in map) {
                                list1.add(ItemGroup(key, value))
                            }
                        }
                        list = list1
                        list1.add(ItemGroup("+1","Add New Item"))
                        itemGroups.postValue(CityResource.success(list1))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    itemGroups.value = CityResource.error(error.message)
                }
            })

        return itemGroups
    }

    fun createNewItem(context: Context,newItemGroupName: String) {
        list.forEach {
            if (it.groupName.trimEnd().compareTo(newItemGroupName.trimEnd(), true) != 0) {
                Toast.makeText(context, "The group already exists!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        Toast.makeText(
            context,
            "New Item Group will be added shortly",
            Toast.LENGTH_SHORT
        ).show()
        FirebaseDatabase.getInstance().getReference("itemgroups/${FirebaseAuth.getInstance().uid}").push().setValue(newItemGroupName)
    }
}