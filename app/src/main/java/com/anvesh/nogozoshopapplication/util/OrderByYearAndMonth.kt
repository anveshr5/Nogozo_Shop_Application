package com.anvesh.nogozoshopapplication.util

import com.anvesh.nogozoshopapplication.datamodels.VendorStats

class OrderByYearAndMonth : Comparator<VendorStats> {
    override fun compare(o1: VendorStats?, o2: VendorStats?): Int {
        if (o1 == null || o2 == null)
            return 0
        return if (o1.id.take(4) == o2.id.take(4)) {
            o2.id.takeLast(2).compareTo(o1.id.takeLast(2))
        } else {
            o1.id.take(4).compareTo(o2.id.take(4))
        }
    }
}