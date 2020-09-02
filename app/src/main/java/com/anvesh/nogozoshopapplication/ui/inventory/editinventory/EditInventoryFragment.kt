package com.anvesh.nogozoshopapplication.ui.inventory.editinventory

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.Item
import com.anvesh.nogozoshopapplication.ui.BaseFragment
import com.anvesh.nogozoshopapplication.ui.inventory.InventoryCommunicator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class EditInventoryFragment(
    private val communicator: InventoryCommunicator
): BaseFragment(R.layout.fragment_edit_inventory), View.OnClickListener {

    //@Inject
    //lateinit var factory: ViewModelFactory

    private lateinit var viewModel: EditInventoryFragmentViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[EditInventoryFragmentViewModel::class.java]

        itemName = view.findViewById(R.id.editinventory_name_field)
        itemPrice = view.findViewById(R.id.editinventory_price_field)
        itemMRP = view.findViewById(R.id.editinventory_mrp_field)
        itemQuantity = view.findViewById(R.id.editinventory_quantity_field)
        header = view.findViewById(R.id.editinventory_header)
        imageView = view.findViewById(R.id.editinventory_imageview)
        imageButton = view.findViewById(R.id.editinventory_imagebutton)
        imageButton.setOnClickListener(this)
        done = view.findViewById(R.id.editinventory_done)
        done.setOnClickListener(this)

        Log.d("change","here")
        getItem()
    }

    private fun getItem(): Boolean{
        if(arguments != null){
            if(arguments!!.containsKey("item")){
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

    private fun setDataToViews(item: Item){
        header.text = "Change Item Details"
        itemName.setText(item.itemName)
        itemPrice.setText(item.itemPrice)
        itemMRP.setText(item.itemMRP)
        itemQuantity.setText(item.itemQuantity)
        val imageRef = FirebaseStorage.getInstance().reference.child("items/${item.itemId}")
        Glide.with(this)
            .load(imageRef)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.editinventory_imagebutton -> {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setRequestedSize(100,100)
                    .start(context!!, this)
            }
            R.id.editinventory_done -> {
                val name = itemName.text.toString()
                val price = itemPrice.text.toString()
                val mrp = itemMRP.text.toString()
                val quantity = itemQuantity.text.toString()
                if(name.isEmpty() || price.isEmpty() || quantity.isEmpty()|| mrp.isEmpty()){
                    Toast.makeText(context, "Please Enter all details", Toast.LENGTH_SHORT).show()
                    return
                }
                val map: HashMap<String, Any> = HashMap()
                map["itemname"] = name
                map["itemprice"] = price
                map["itemMRP"] = mrp
                map["quantity"] = quantity
                if(isNew){
                    map["isAvailable"] = "true"
                    viewModel.createItem(map, imageUri).addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
                            communicator.onBackPressedFromEdit()
                        }
                    }
                }else{
                    if(oldItem!!.equalsTo(newItem) && !imageChange)
                        return

                    viewModel.updateItem(oldItem!!.itemId!!, map, imageUri).addOnCompleteListener{
                        if(it.isSuccessful){
                            Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
                            communicator.onBackPressedFromEdit()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if(resultCode == RESULT_OK){
                    imageChange = true
                    imageUri = result.uri
                    imageView.setImageURI(imageUri)
                }else{
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}