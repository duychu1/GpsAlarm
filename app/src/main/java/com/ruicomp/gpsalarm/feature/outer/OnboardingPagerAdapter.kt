package com.ruicomp.gpsalarm.feature.outer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ruicomp.gpsalarm.R

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity, private val totalItems: Int) :
    FragmentStateAdapter(fragmentActivity) {

    private val mFragmentList: MutableList<Fragment> = ArrayList()


    override fun getItemCount(): Int {
        return totalItems
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    fun removeFag(pos: Int) {
        if (pos in mFragmentList.indices)
            mFragmentList.removeAt(pos)
    }

    fun clear() {
        mFragmentList.clear()
    }

    override fun getItemId(position: Int): Long {
        return mFragmentList[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        val pageIds = mFragmentList.map { it.hashCode().toLong() }
        return pageIds.contains(itemId)
    }
}
