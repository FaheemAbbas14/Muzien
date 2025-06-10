package com.tt.muzien.data.dto

import com.tt.muzien.data.responses.SaloonImage
import com.tt.muzien.data.responses.SaloonWorkHour


/**
 * Created by Faheem Abbas on 03/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class SaloonDto(
    val id: Int, val icon: List<SaloonImage?>?=listOf<SaloonImage>(), val name: String, val isOpened: Boolean, val isActive: Boolean, val location: String, val ratings: String, val timing: List<SaloonWorkHour?>?,
    val latitude: Double? =0.0,
    val longitude: Double? =0.0)
