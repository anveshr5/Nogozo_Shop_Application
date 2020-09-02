package com.anvesh.nogozoshopapplication.ui.splash

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter constructor(fm: FragmentManager): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        TODO("Not yet implemented")
//        when(position){
//            0 -> {return fragmnet for 0}
//
//        }
//        ||
//        return fragmentArray[position]
    }

    override fun getCount(): Int {
        return 0
    }
}