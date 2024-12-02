package com.tt.muzien.data.repository

import com.tt.muzien.constants.Keys
import com.tt.muzien.data.network.AuthApi
import com.tt.muzien.utilities.PreferenceManager


class AuthRepository(
    private val api: AuthApi,
    private val preferences: PreferenceManager
) : BaseRepository() {

    suspend fun login(
        email: String,
        password: String
    ) = safeApiCall {
        api.login(email, password)
    }

    suspend fun saveAuthToken(token: String) {
        preferences.putString(Keys.Access_Token, token)
    }
}