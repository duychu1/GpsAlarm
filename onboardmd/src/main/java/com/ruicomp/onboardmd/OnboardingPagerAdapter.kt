package com.ruicomp.onboardmd

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity, private val totalItems: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return totalItems
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardContentFragment.newInstance(
                0,
                R.drawable.ic_placeholder1,
                R.string.onboard_title_1,
                R.string.onboard_desc_1
            )
            1 -> OnboardAdFragment.newInstance()
            2 -> OnboardContentFragment.newInstance(
                1,
                R.drawable.ic_placeholder1,
                R.string.onboard_title_2,
                R.string.onboard_desc_2
            )
            3 -> OnboardAdFragment.newInstance()
            4 -> OnboardContentFragment.newInstance(
                2,
                R.drawable.ic_placeholder1,
                R.string.onboard_title_3,
                R.string.onboard_desc_3
            )
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
