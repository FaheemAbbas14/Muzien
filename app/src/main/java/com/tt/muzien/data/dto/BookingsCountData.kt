package com.tt.muzien.data.dto


/**
 * Created by Faheem Abbas on 23/04/2025.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class BookingsCountData(  val pendingApproval: Long,
                               val scheduled: Long,
                               val overdue: Long,
                               val completed: Long,
                               val cancelled: Long,)
