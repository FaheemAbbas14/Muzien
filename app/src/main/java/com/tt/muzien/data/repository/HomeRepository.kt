package com.tt.muzien.data.repository

import com.tt.muzien.constants.Keys
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.data.network.HomeApi
import com.tt.muzien.utilities.PreferenceManager


/**
 * Created by Faheem Abbas on 26/11/2024.
 * Technical Lead
 * Bajco Technologies
 * faheem.abbas@bajcotechnologies.com
 * +923115284424
 */
class HomeRepository(
    private val api: HomeApi,
    private val preferences: PreferenceManager
) : BaseRepository() {


}