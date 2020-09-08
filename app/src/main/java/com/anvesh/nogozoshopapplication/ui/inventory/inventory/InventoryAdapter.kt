package com.anvesh.nogozoshopapplication.ui.inventory.inventory

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.Item
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.storage.FirebaseStorage

class InventoryAdapter(private var inventoryItemClick: OnInventoryItemClick) :
    RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    private var items: List<Item> = arrayListOf()
    private val itemImageBaseUrl = FirebaseStorage.getInstance().reference.child("items")
    private val comparator = Comparator<String> { o1, o2 ->
        o1.compareTo(o2, false)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_inventory, parent, false)
        return InventoryViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.itemName.text = items[position].itemName
        holder.itemPrice.text = "₹${items[position].itemPrice}"
        if (items[position].itemMRP == null || items[position].itemMRP == items[position].itemPrice) {
            holder.itemMRP.visibility = View.INVISIBLE
        } else {
            holder.itemMRP.visibility = View.VISIBLE
            holder.itemMRP.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.itemMRP.text = "₹${items[position].itemMRP}"
        }
        holder.itemQuantity.text = items[position].itemQuantity
        items[position].isAvailable?.let {
            holder.itemSwitch.isChecked = it
        }
        Glide.with(holder.itemView.context)
            .load(itemImageBaseUrl.child(items[position].itemId!!))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(holder.itemImage)
    }

    fun setDataList(items: ArrayList<Item>) {
        this.items = items.sortedWith(compareBy<Item> { it.itemGroup }.thenBy { it.itemName })
        notifyDataSetChanged()
    }

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val itemName: TextView = itemView.findViewById(R.id.list_item_inventory_name)
        val itemPrice: TextView = itemView.findViewById(R.id.list_item_inventory_price)
        val itemMRP: TextView = itemView.findViewById(R.id.list_item_inventory_mrp)
        val itemQuantity: TextView = itemView.findViewById(R.id.list_item_inventory_quantity)
        val itemImage: ImageView = itemView.findViewById(R.id.list_item_inventory_image)
        val itemSwitch: SwitchMaterial = itemView.findViewById(R.id.list_item_inventory_switch)

        init {
            itemView.setOnClickListener(this)
            itemSwitch.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
                inventoryItemClick.onAvailabililtyChanged(
                    items[adapterPosition].itemId!!,
                    b.toString()
                )
            }
        }

        override fun onClick(v: View?) {
            inventoryItemClick.onInventoryItemClick(items[adapterPosition])
        }
    }

    interface OnInventoryItemClick {
        fun onInventoryItemClick(item: Item)

        fun onAvailabililtyChanged(itemId: String, newAvailabilityStatus: String)
    }
}