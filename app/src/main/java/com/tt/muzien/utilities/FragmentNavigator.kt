package com.tt.muzien.utilities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.tt.muzien.R
import java.util.Locale


/**
 * Created by Faheem Abbas on 07/08/2024.
 * Technical Lead
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */

class FragmentNavigator {

    fun loadFragment(
        newFragment: Fragment,
        fragmentManager: FragmentManager,
        container: Int = R.id.fragment_container,
        usedVerticalAnimation: Boolean = false,
    ) {
        val transaction = fragmentManager.beginTransaction()
        val tag = newFragment::class.java.simpleName

        if (!usedVerticalAnimation) {
            val isArabic = Locale.getDefault().language == "ar"
            if (isArabic) {
                // Right-to-left animations
                transaction.setCustomAnimations(
                    R.anim.fragment_enter_rtl,
                    R.anim.fragment_exit_rtl,
                    R.anim.fragment_pop_enter_rtl,
                    R.anim.fragment_pop_exit_rtl
                )
            } else {
                // Left-to-right animations
                transaction.setCustomAnimations(
                    R.anim.fragment_enter,
                    R.anim.fragment_exit,
                    R.anim.fragment_pop_enter,
                    R.anim.fragment_pop_exit
                )
            }
        } else {
            // Bottom-to-top animations
            transaction.setCustomAnimations(
                R.anim.fragment_enter_bottom,
                R.anim.fragment_exit_bottom,
                R.anim.fragment_pop_enter_bottom,
                R.anim.fragment_pop_exit_bottom
            )
        }

        // Check if fragment already exists
        val existingFragment = fragmentManager.findFragmentByTag(tag)
        if (existingFragment != null) {
            // Show existing instead of adding new
            fragmentManager.fragments.filter { it.isVisible }.forEach { transaction.hide(it) }
            transaction.show(existingFragment)
        } else {
            // Hide current and add new
            fragmentManager.fragments.filter { it.isVisible }.forEach { transaction.hide(it) }
            transaction.add(container, newFragment, tag)
            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }

    fun popFragment(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        }
    }
}
