package com.tt.muzien.data.dto


/**
 * Created by Faheem Abbas on 25/12/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
data class FilterData(val selection: String,val from: String,val to: String,val fromRevenue: Boolean=false,val bookingStatus: String="",val serviceProvider: String="",val saloon: String="")
