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
class FragmentManager {
    fun loadFragment(newFragment: Fragment, supportFragmentManager: FragmentManager) {
        // FragmentUtils.getInstance().addFragment(this, fragment, R.id.fragment_container, true)
        val fragmentTransaction = supportFragmentManager.beginTransaction()

//        // Hide all fragments
//        supportFragmentManager.fragments.forEach { fragment ->
//            fragmentTransaction.hide(fragment)
//        }
//
//        val fragmentTag = newFragment::class.java.simpleName
//        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
//        // Check if the fragment is already added
//        if (existingFragment != null) {
//            fragmentTransaction.add(R.id.fragment_container, newFragment, fragmentTag)
//        } else {
//            fragmentTransaction.add(R.id.fragment_container, newFragment, fragmentTag)
//        }
        fragmentTransaction.replace(R.id.fragment_container, newFragment)
            .addToBackStack(null)
            .commit()
//        fragmentTransaction.addToBackStack(null).commit()
    }

    fun popFragment(supportFragmentManager: FragmentManager) {
        supportFragmentManager.popBackStack()
    }

    fun loadFragment(
        newFragment: Fragment,
        supportFragmentManager: FragmentManager,
        container: Int
    ) {
        // FragmentUtils.getInstance().addFragment(this, fragment, R.id.fragment_container, true)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
//
//        // Hide all fragments
//        supportFragmentManager.fragments.forEach { fragment ->
//            fragmentTransaction.hide(fragment)
//        }
//        val fragmentTag = newFragment::class.java.simpleName
//        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
//        // Check if the fragment is already added
//        if (existingFragment != null) {
//            fragmentTransaction.add(container, newFragment, fragmentTag)
//        } else {
//            fragmentTransaction.add(container, newFragment, fragmentTag)
//        }
//
//        fragmentTransaction.addToBackStack(null).commit()

        fragmentTransaction.replace(container, newFragment)
            .addToBackStack(null)
            .commit()
    }

}