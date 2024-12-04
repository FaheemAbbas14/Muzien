package com.tt.muzien.data.dto

import com.tt.muzien.enums.EnumNotificationType


/**
 * Created by Faheem Abbas on 03/12/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class NotificationDto(val profileUrl: String?=null,val icon: Int?=null,val heading: String,val description: String,val type: EnumNotificationType)
