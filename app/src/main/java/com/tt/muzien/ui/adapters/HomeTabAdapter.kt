package com.tt.muzien.ui.adapters


/**
 * Created by Faheem Abbas on 27/11/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tt.muzien.ui.analytics.FragmentAnalytics
import com.tt.muzien.ui.member.FragmentMembers
import com.tt.muzien.ui.saloon.FragmentSaloon
import com.tt.muzien.ui.service.FragmentServices

class HomeTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4 // Number of tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentAnalytics()
            1 -> FragmentSaloon()
            2 -> FragmentMembers()
            3 -> FragmentServices()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
