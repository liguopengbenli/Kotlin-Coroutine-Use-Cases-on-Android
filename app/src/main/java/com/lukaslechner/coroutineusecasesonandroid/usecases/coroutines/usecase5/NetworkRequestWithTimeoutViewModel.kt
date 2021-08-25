package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.lang.Exception

class NetworkRequestWithTimeoutViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest(timeout: Long) {
        uiState.value = UiState.Loading
        usingWithTimeOutOrNull(timeout)
    }

    private fun usingWithTimeOut(timeout: Long) {
        viewModelScope.launch {
            try {
                val recentAndroidVersion = withTimeout(timeout) {
                    api.getRecentAndroidVersions()
                }
                uiState.value = UiState.Success(recentAndroidVersion)
            } catch (timeoutCancellationException: TimeoutCancellationException) {
                uiState.value = UiState.Error("Network request timeout")
            } catch (e: Exception) {
                Timber.e(e)
                uiState.value = UiState.Error("Network failed")
            }
        }
    }

    private fun usingWithTimeOutOrNull(timeout: Long) {
        viewModelScope.launch {
            try {
                val recentAndroidVersion = withTimeoutOrNull(timeout) {
                    api.getRecentAndroidVersions()
                }
                if (recentAndroidVersion != null) {
                    uiState.value = UiState.Success(recentAndroidVersion)
                } else {
                    uiState.value = UiState.Error("Network timeout")
                }
            }  catch (e: Exception) {
                Timber.e(e)
                uiState.value = UiState.Error("Network failed")
            }
        }
    }

}