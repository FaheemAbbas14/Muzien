package com.tt.muzien.utilities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.tt.muzien.R


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
        container: Int? = R.id.fragment_container
    ) {
        val transaction = fragmentManager.beginTransaction()

        // Optional: Hide the currently visible fragment
        fragmentManager.fragments
            .filter { it.isVisible }
            .forEach { transaction.hide(it) }

        val tag = newFragment::class.java.simpleName
        transaction.add(container!!, newFragment, tag)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    fun popFragment(fragmentManager: FragmentManager) {
        fragmentManager.popBackStack()
    }


}