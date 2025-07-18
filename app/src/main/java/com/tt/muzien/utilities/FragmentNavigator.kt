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
        container: Int? = R.id.fragment_container,
        usedVerticallyAnimation: Boolean = false,
    ) {
        val transaction = fragmentManager.beginTransaction()
        if (!usedVerticallyAnimation) {
            val isArabic = Locale.getDefault().language == "ar"
            if (isArabic) {
                transaction.setCustomAnimations(
                    R.anim.fragment_out,
                    R.anim.fragment_out,
                    R.anim.fragment_enter,
                    R.anim.fragment_enter
                )
            } else {
                // Set custom animations  left to right
                transaction.setCustomAnimations(
                    R.anim.fragment_enter,
                    R.anim.fragment_enter,
                    R.anim.fragment_out,
                    R.anim.fragment_out
                )
            }
        } else {
            // Set custom animations  bottom to top
            transaction.setCustomAnimations(
                R.anim.fragment_enter_bottom,
                R.anim.fragment_enter_bottom,
                R.anim.fragment_out_bottom,
                R.anim.fragment_out_bottom
            )
        }
        // Optional: Hide the currently visible fragment
        fragmentManager.fragments
            .filter { it.isVisible }
            .forEach { transaction.hide(it) }

        val tag = newFragment::class.java.simpleName
        transaction.add(container!!, newFragment, tag)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    fun popFragment(supportFragmentManager: FragmentManager) {
        try {
            val transaction = supportFragmentManager.beginTransaction()

            // Set custom animations  left to right
            // transaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_out)
            // Set custom animations  bottom to top
            transaction.setCustomAnimations(
                R.anim.fragment_enter_bottom,
                R.anim.fragment_out_bottom
            )

// Pop the fragment from the back stack
            supportFragmentManager.popBackStackImmediate()

// Commit the transaction
            transaction.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}