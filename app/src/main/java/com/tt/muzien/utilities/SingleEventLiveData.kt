package com.tt.muzien.utilities

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


/**
 * Created by Faheem Abbas on 10/07/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class SingleEventLiveData<T> : MutableLiveData<T>() {

    private var pending = false

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer { data ->
            if (pending) {
                pending = false
                observer.onChanged(data)
            }
        })
    }

    override fun setValue(value: T) {
        pending = true
        super.setValue(value)
    }

    override fun postValue(value: T) {
        pending = true
        super.postValue(value)
    }
}