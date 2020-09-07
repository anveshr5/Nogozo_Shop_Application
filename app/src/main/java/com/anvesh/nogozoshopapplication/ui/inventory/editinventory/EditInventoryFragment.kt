package com.anvesh.nogozoshopapplication.ui.inventory.editinventory

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.Item
import com.anvesh.nogozoshopapplication.datamodels.ItemGroup
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.inventory.InventoryCommunicator
import com.anvesh.nogozoshopapplication.ui.userdetails.CityResource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class EditInventoryFragment(
    private val communicator: InventoryCommunicator
) : BaseFragment(R.layout.fragment_edit_inventory), View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: EditInventoryFragmentViewModel

    private lateinit var itemGroupSpinner: TextView
    private lateinit var itemGroupCard: CardView

    private lateinit var itemName: TextInputEditText
    private lateinit var itemPrice: TextInputEditText
    private lateinit var itemMRP: TextInputEditText
    private lateinit var itemQuantity: TextInputEditText
    private lateinit var imageView: ImageView
    private lateinit var imageButton: Button
    private lateinit var done: MaterialButton
    private lateinit var header: TextView

    private var oldItem: Item? = null
    private var newItem: Item? = null
    private var isNew: Boolean = true
    private var imageChange = false
    private var imageUri: Uri? = null
    private var selectedItemGroup: ItemGroup? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[EditInventoryFragmentViewModel::class.java]

        itemName = view.findViewById(R.id.editinventory_name_field)
        itemPrice = view.findViewById(R.id.editinventory_price_field)
        itemMRP = view.findViewById(R.id.editinventory_mrp_field)
        itemQuantity = view.findViewById(R.id.editinventory_quantity_field)
        header = view.findViewById(R.id.editinventory_header)
        imageView = view.findViewById(R.id.editinventory_imageview)
        itemGroupCard = view.findViewById(R.id.editinventory_item_group_wrapper)
        itemGroupSpinner = view.findViewById(R.id.editinventory_item_group_view)
        itemGroupSpinner.setOnClickListener(this)
        imageButton = view.findViewById(R.id.editinventory_imagebutton)
        imageButton.setOnClickListener(this)
        done = view.findViewById(R.id.editinventory_done)
        done.setOnClickListener(this)

        Log.d("change", "here")
        getItem()
    }

    private fun getItem(): Boolean {
        if (arguments != null) {
            if (arguments!!.containsKey("item")) {
                oldItem = arguments!!.getSerializable("item") as Item
                newItem = arguments!!.getSerializable("item") as Item
                setDataToViews(oldItem!!)
                isNew = false
                return true
            }
        }
        newItem = Item()
        isNew = true
        return false
    }

    private fun setDataToViews(item: Item) {
        header.text = "Change Item Details"
        itemName.setText(item.itemName)
        itemPrice.setText(item.itemPrice)
        itemMRP.setText(item.itemMRP)
        itemQuantity.setText(item.itemQuantity)
        if (item.itemGroup != "")
            itemGroupSpinner.text = item.itemGroup
        else
            itemGroupSpinner.text = "Select a group"
        val imageRef = FirebaseStorage.getInstance().reference.child("items/${item.itemId}")
        Glide.with(this)
            .load(imageRef)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)
    }

    private fun onItemGroupSelected(itemGroup: ItemGroup) {
        selectedItemGroup = itemGroup
        itemGroupSpinner.text = itemGroup.groupName
    }

    private fun openDialogForSelecting(type: String) {
        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.dialog_select_from_list)

        val searchView: SearchView = dialog.findViewById(R.id.dialog_searchview)
        val listView: ListView = dialog.findViewById(R.id.dialog_listview)
        val progressBar: ProgressBar = dialog.findViewById(R.id.dialog_progressbar)
        val header: TextView = dialog.findViewById(R.id.dialog_header)

        if (type == "Select Item Group") {
            header.text = "Choose A Group"
            viewModel.getItemGroups().removeObservers(viewLifecycleOwner)

            viewModel.getItemGroups().observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    CityResource.CityStatus.SUCCESS -> {
                        progressBar.visibility = View.INVISIBLE

                        searchView.setQuery("", false)
                        val adapter = ItemGroupListAdapter()
                        listView.adapter = adapter
                        adapter.setOriginalList(it.data)

                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                adapter.getFilter().filter(newText)
                                return true
                            }
                        })

                        listView.onItemClickListener =
                            AdapterView.OnItemClickListener { parent, view, position, id ->
                                if (adapter.getItem(position).groupId != "-1" && adapter.getItem(
                                        position
                                    ).groupId != "+1"
                                ) {
                                    onItemGroupSelected(adapter.getItem(position))
                                    dialog.dismiss()
                                } else if (adapter.getItem(position).groupId == "+1") {
                                    Toast.makeText(
                                        activity as Context,
                                        "Creating new Group",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val newItemGroupDialog = Dialog(context!!)
                                    newItemGroupDialog.setContentView(R.layout.dialog_add_item_group)
                                    val newGroupName =
                                        newItemGroupDialog.findViewById<EditText>(R.id.editText)
                                    val confirmButton =
                                        newItemGroupDialog.findViewById<Button>(R.id.btnAddNewGroup)
                                    val cancelButton =
                                        newItemGroupDialog.findViewById<Button>(R.id.btnCancel)
                                    confirmButton.setOnClickListener {
                                        if (newGroupName.text.toString() != "") {
                                            //Database().createItemGroup(FirebaseAuth.getInstance().uid!!,newGroupName.text.toString())
                                            viewModel.createNewItem(
                                                activity as Context,
                                                newGroupName.text.toString()
                                            )
                                            newItemGroupDialog.dismiss()
                                        }
                                    }
                                    cancelButton.setOnClickListener {
                                        newItemGroupDialog.dismiss()
                                    }
                                    newItemGroupDialog.show()
                                }
                                //}
                            }
                    }
                    CityResource.CityStatus.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    CityResource.CityStatus.ERROR -> {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG).show()
                    }
                }
            })
        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.editinventory_imagebutton -> {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setRequestedSize(100, 100)
                    .start(context!!, this)
            }
            R.id.editinventory_done -> {
                val name = itemName.text.toString()
                val price = itemPrice.text.toString()
                val mrp = itemMRP.text.toString()
                val quantity = itemQuantity.text.toString()
                val itemGroupSelected = selectedItemGroup
                if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || mrp.isEmpty()) {
                    Toast.makeText(context, "Please Enter all details", Toast.LENGTH_SHORT).show()
                    return
                }
                val map: HashMap<String, Any> = HashMap()
                map["itemname"] = name
                map["itemprice"] = price
                map["itemMRP"] = mrp
                map["quantity"] = quantity
                if (selectedItemGroup != null && selectedItemGroup!!.groupName != "")
                    map["itemGroup"] = selectedItemGroup!!.groupName
                if (isNew) {
                    map["isAvailable"] = "true"
                    viewModel.createItem(map, imageUri).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
                            communicator.onBackPressedFromEdit()
                        }
                    }
                } else {
                    if (oldItem!!.equalsTo(newItem) && !imageChange)
                        return

                    viewModel.updateItem(oldItem!!.itemId!!, map, imageUri).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
                            communicator.onBackPressedFromEdit()
                        }
                    }
                }
            }
            R.id.editinventory_item_group_view -> {
                openDialogForSelecting("Select Item Group")
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    imageChange = true
                    imageUri = result.uri
                    imageView.setImageURI(imageUri)
                } else {
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}