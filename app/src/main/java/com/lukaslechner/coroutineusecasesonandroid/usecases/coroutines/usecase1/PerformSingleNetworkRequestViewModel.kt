package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class PerformSingleNetworkRequestViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performSingleNetworkRequest() {
        uiState.value = UiState.Loading
        // execute on the main thread
        // we switch thread when do expensive computation
        viewModelScope.launch {
            try {
                val recentAndroidVersion = mockApi.getRecentAndroidVersions()
                uiState.value = UiState.Success(recentAndroidVersion)
            } catch (e: Exception) {
                Timber.e(e)
                uiState.value = UiState.Error("Network failed")
            }

        }
    }
}