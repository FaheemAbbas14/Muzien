package com.tt.muzien.data.dto

import android.net.Uri


/**
 * Created by Faheem Abbas on 18/03/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
object AddSaloonData {
    var startTime: String? = null
    var image_uris = ArrayList<Uri>()
    var certificate_uri: Uri? = null
    var endTime: String? = null
    var days = ArrayList<String>()
    var holidays = ArrayList<String>()
    var address: String? = null
    var addressLat: Double? = 0.0
    var addressLng: Double? = 0.0
    var country: String? = null

    fun clear() {
        startTime = null
        image_uris.clear()
        certificate_uri = null
        endTime = null
        days.clear()
        holidays.clear()
        address = null
        addressLat = 0.0
        addressLng = 0.0
        country = null
    }
}