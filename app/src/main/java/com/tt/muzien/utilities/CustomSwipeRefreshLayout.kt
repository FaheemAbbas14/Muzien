package com.tt.muzien.utilities

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


/**
 * Created by Faheem Abbas on 23/05/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class CustomSwipeRefreshLayout(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {
    var listView: ListView? = null
    var recyclerView: RecyclerView? = null
    override fun canChildScrollUp(): Boolean {
        // You can customize this for ExtendListView
        if (listView != null) {
            return listView?.let {
                it.childCount > 0 && (it.firstVisiblePosition > 0 || it.getChildAt(0).top < it.paddingTop)
            } ?: super.canChildScrollUp()
        } else {
            return recyclerView?.canScrollVertically(-1) ?: super.canChildScrollUp()
        }
    }
}