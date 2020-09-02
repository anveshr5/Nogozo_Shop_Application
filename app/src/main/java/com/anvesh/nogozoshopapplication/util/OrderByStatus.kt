package com.anvesh.nogozoshopapplication.util

import com.anvesh.nogozoshopapplication.datamodels.Order

class OrderByStatus : Comparator<Order> {
    override fun compare(o1: Order?, o2: Order?): Int {
        if (o1 == null || o2 == null)
            return 0
        if (o1.status != "0" && o2.status != "0") {
            if (o1.status.compareTo(o2.status) == 0)
                return o2.datetime.compareTo(o1.datetime)
            return o1.status.compareTo(o2.status)
        } else {
            if (o1.status.compareTo(o2.status) == 0)
                return o1.datetime.compareTo(o2.datetime)
            return o1.status.compareTo(o2.status)
        }
    }
}