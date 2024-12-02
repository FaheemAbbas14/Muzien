package com.tt.muzien.data.repository

import com.tt.muzien.data.network.UserApi


class UserRepository(
    private val api: UserApi,
) : BaseRepository() {

 suspend fun getUser() = safeApiCall {
   api.getUser()

 }
}
