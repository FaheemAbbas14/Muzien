package com.tt.muzien.data.dto


/**
 * Created by Faheem Abbas on 25/12/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
data class FilterData(val selection: String, var from: String?, var to: String?, val fromRevenue: Boolean=false,
                      var bookingStatus: String?=null, val serviceProvider: String?=null, val saloon: String="", val saloonId: Int=0)
