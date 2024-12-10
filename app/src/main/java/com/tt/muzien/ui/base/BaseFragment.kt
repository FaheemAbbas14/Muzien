package com.tt.muzien.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.tt.muzien.constants.Keys
import com.tt.muzien.data.network.RemoteDataSource
import com.tt.muzien.data.network.UserApi
import com.tt.muzien.data.repository.BaseRepository
import com.tt.muzien.ui.auth.AuthActivity
import com.tt.muzien.ui.startNewActivity
import com.tt.muzien.utilities.PreferenceManager
import kotlinx.coroutines.launch

/**
 * Base Fragment class for all fragments
 *
 * @param VM ViewModel class to be instantiated
 * @param B ViewBinding class to be instantiated
 * @param R Repository class for specific flows
 */
abstract class BaseFragment<VM : BaseViewModel, B : ViewBinding, R : BaseRepository> : Fragment() {

    protected lateinit var userPreferences: PreferenceManager
    protected lateinit var binding: B
    protected val remoteDataSource = RemoteDataSource()
    protected lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userPreferences = PreferenceManager.getInstance(requireContext())
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
//        lifecycleScope.launch {
//            userPreferences.getString(Keys.Access_Token)?.first()
//        }
        return binding.root

    }

    fun logout() {
        lifecycleScope.launch {
            val authToken = userPreferences.getString(Keys.Access_Token)
            val api = remoteDataSource.buildApi(UserApi::class.java, authToken)
            viewModel.logout(api)
            userPreferences.putString(Keys.Access_Token, "")
            requireActivity().startNewActivity(AuthActivity::class.java)
            requireActivity().finish()
        }
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getFragmentRepository(): R

}