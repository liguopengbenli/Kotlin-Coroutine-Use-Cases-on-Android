package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import android.util.Log.e
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.e
import java.lang.Exception
import java.util.*

class RetryNetworkRequestViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            val numberOfRetries = 2
            try {
                // if we catch a exception we will leave repeat function
                // so we need another try and catch block
                retry(numberOfRetries) {
                    loadRecentAndroidVersion()
                }

            } catch (e: Exception) {
                Timber.e(e)
                uiState.value = UiState.Error("Network failed")
            }

        }
    }

    private suspend fun <T> retry(
        numberOfRetries: Int,
        initialDelayMillis: Long = 100,
        maxDelayMillis: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMillis
        repeat(numberOfRetries) {
            try {
                block()
            } catch (e: Exception) {
                Timber.e(e)
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
        }
        return block()
    }

    private suspend fun loadRecentAndroidVersion() {
        val recentAndroidVersion = api.getRecentAndroidVersions()
        uiState.value = UiState.Success(recentAndroidVersion)
    }

}