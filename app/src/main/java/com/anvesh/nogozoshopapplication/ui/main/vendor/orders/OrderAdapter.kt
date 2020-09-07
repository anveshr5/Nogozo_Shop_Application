package com.anvesh.nogozoshopapplication.ui.main.vendor.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozoshopapplication.R
import com.anvesh.nogozoshopapplication.datamodels.Order
import com.anvesh.nogozoshopapplication.network.Database
import com.anvesh.nogozoshopapplication.util.OrderByStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderAdapter(
    private val showPackedButton: Boolean = false,
    private val comparator: Comparator<Order> = OrderByStatus()
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orderList: ArrayList<Order> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_order, parent, false)
        return OrderViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        //if (showPackedButton)
        holder.shopName.visibility = View.GONE
        holder.shopName.text = orderList[position].shopname
        holder.dateTime.text = "${orderList[position].date} on ${orderList[position].time}"
        holder.customerName.text = orderList[position].customername
        if (orderList[position].deliverycharges != "0")
            holder.price.text =
                "₹${orderList[position].itemprice} + ₹${orderList[position].deliverycharges}(Delivery Charges)"
        else
            holder.price.text = "₹${orderList[position].itemprice}"

        holder.instruction.text = orderList[position].shopinstruction

        if (orderList[position].shopinstruction.isNullOrBlank())
            holder.instruction.visibility = View.GONE

        holder.deliveryMode.text = if (orderList[position].delivery == "Yes") {
            "Deliver"
        } else {
            "Pickup"
        }

        if (orderList[position].delivery == "Yes") {
            holder.customerPhoneTag.visibility = View.VISIBLE
            holder.customerAddressTag.visibility = View.VISIBLE
            holder.customerPhone.text = orderList[position].customerphone
            holder.customerAddress.text = orderList[position].customeraddress
        }

        //items stored in map under each order
        var items = ""
        for ((key, values) in orderList[position].items) {
            val item = values as HashMap<String, String>
            items += "${item["times"]} x ${item["itemname"]}(${item["quantity"]})\n"
        }
        items = items.removeSuffix("\n")
        holder.items.text = items

        holder.orderId.text = orderList[position].orderId



        if (orderList[position].delivery == "Yes") {
            when (orderList[position].status) {
                "0" -> {
                    holder.btnCancelOrder.visibility = View.VISIBLE
                    holder.markedPacked.text = "Mark Packed"
                    holder.status.text = "New Order"
                    holder.markedPacked.visibility = View.VISIBLE
                }
                "1" -> {
                    holder.btnCancelOrder.visibility = View.VISIBLE
                    holder.status.text = "Delivery Executive will pickup Order"
                    holder.markedPacked.text = "Mark Delivered"
                }
                "2" -> {
                    holder.btnCancelOrder.visibility = View.VISIBLE
                    holder.markedPacked.text = "Mark Delivered"
                    holder.status.text = "Out for delivery"
                }
                "3" -> {
                    holder.markedPacked.visibility = View.GONE
                    holder.status.text = "Delivered"
                    holder.btnCancelOrder.visibility = View.GONE
                }
                "-1" -> {
                    holder.markedPacked.visibility = View.GONE
                    holder.status.text = "Order Cancelled"
                    holder.btnCancelOrder.visibility = View.GONE
                }
            }
        } else {
            when (orderList[position].status) {
                "0" -> {
                    holder.btnCancelOrder.visibility = View.VISIBLE
                    holder.status.text = "New Order"
                    holder.markedPacked.visibility = View.VISIBLE
                    holder.markedPacked.text = "Mark Packed"
                }
                "1" -> {
                    holder.btnCancelOrder.visibility = View.VISIBLE
                    holder.markedPacked.text = "Mark Done"
                    holder.status.text = "Customer will pickup the order"
                }
                "3" -> {
                    holder.markedPacked.visibility = View.GONE
                    holder.status.text = "Picked Up"
                    holder.btnCancelOrder.visibility = View.GONE
                }
                "-1" -> {
                    holder.markedPacked.visibility =View.GONE
                    holder.status.text = "Order Cancelled"
                    holder.status.visibility = View.GONE
                }
            }
        }
    }

    fun setList(dataList: ArrayList<Order>) {
        orderList = dataList
        CoroutineScope(Dispatchers.Default).launch {
            orderList.sortWith(comparator)
            withContext(Main) {
                notifyDataSetChanged()
            }
        }
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val orderId: TextView = itemView.findViewById(R.id.list_item_current_order_id)
        val shopName: TextView = itemView.findViewById(R.id.list_item_current_order_shopname)
        val dateTime: TextView = itemView.findViewById(R.id.list_item_current_order_date_time)
        val price: TextView = itemView.findViewById(R.id.list_item_current_order_price)
        val items: TextView = itemView.findViewById(R.id.list_item_current_order_items)
        val status: TextView = itemView.findViewById(R.id.list_item_current_order_status)
        val instruction: TextView =
            itemView.findViewById(R.id.list_item_current_order_instruction)

        val deliveryMode: TextView = itemView.findViewById(R.id.list_item_delivery_mode)
        val customerName: TextView = itemView.findViewById(R.id.list_item_customer_name)
        val customerPhone: TextView = itemView.findViewById(R.id.list_item_customer_phone)
        val customerAddress: TextView = itemView.findViewById(R.id.list_item_customer_address)
        val customerPhoneTag: LinearLayout =
            itemView.findViewById(R.id.list_item_customer_phone_tag)
        val customerAddressTag: LinearLayout =
            itemView.findViewById(R.id.list_item_customer_address_tag)

        val markedPacked: TextView =
            itemView.findViewById(R.id.list_item_current_order_markpacked_button)
        val btnCancelOrder: TextView =
            itemView.findViewById(R.id.list_item_current_order_cancel_button)

        init {
            markedPacked.setOnClickListener(this)
            btnCancelOrder.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when (v!!.id) {
                R.id.list_item_current_order_markpacked_button -> {
                    if (orderList[adapterPosition].delivery == "Yes") {
                        if (orderList[adapterPosition].status == "0") {
                            Database().markedOrderPacked(orderList[adapterPosition].orderkey, "2")
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        orderList[adapterPosition].status = "2"
                                        notifyDataSetChanged()
                                    }
                                }
                        } else if (orderList[adapterPosition].status == "1") {
                            Database().markedOrderPacked(orderList[adapterPosition].orderkey, "2")
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        orderList[adapterPosition].status = "2"
                                        notifyDataSetChanged()
                                    }
                                }
                        } else if (orderList[adapterPosition].status == "2") {
                            Database().markedOrderPacked(orderList[adapterPosition].orderkey, "3")
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        orderList[adapterPosition].status = "3"
                                        notifyDataSetChanged()
                                    }
                                }
                        }
                    } else {
                        if (orderList[adapterPosition].status == "0") {
                            Database().markedOrderPacked(orderList[adapterPosition].orderkey, "1")
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        orderList[adapterPosition].status = "1"
                                        notifyDataSetChanged()
                                    }
                                }
                        } else if (orderList[adapterPosition].status == "1") {
                            Database().markedOrderPacked(orderList[adapterPosition].orderkey, "3")
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        orderList[adapterPosition].status = "3"
                                        notifyDataSetChanged()
                                    }
                                }
                        }
                    }
                }
                R.id.list_item_current_order_cancel_button -> {
                    if (orderList[adapterPosition].status != "3" || orderList[adapterPosition].status != "-1") {
                        Database().markedOrderPacked(orderList[adapterPosition].orderkey, "-1")
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    orderList[adapterPosition].status = "-1"
                                    notifyDataSetChanged()
                                }
                            }
                    }
                }
            }
        }
    }
}
