package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

class TimeoutAndRetryViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        val numberOfRetries = 2
        val timeout = 1000L

        viewModelScope.launch {

            val feature27Deferred = async {
                retryWithTimeOut(numberOfRetries, timeout) {
                    api.getAndroidVersionFeatures(27)
                }
            }
            val feature28Deferred = async {
                retryWithTimeOut(numberOfRetries, timeout) {
                    api.getAndroidVersionFeatures(28)
                }
            }

            try {
                val versionsFeatures = listOf(feature27Deferred, feature28Deferred).awaitAll()
                uiState.value = UiState.Success(versionsFeatures)
            } catch (e: Exception) {
                uiState.value = UiState.Error("error $e")
            }
        }
    }

    private suspend fun <T> retryWithTimeOut(
        numberOfRetries: Int,
        timeout: Long,
        block: suspend () -> T
    ): T {
        return retry(numberOfRetries) {
            withTimeout(timeout) {
                block()
            }
        }
    }

    private suspend fun <T> retry(
        numberOfRetries: Int,
        delayMillis: Long = 100,
        block: suspend () -> T
    ): T {
        repeat(numberOfRetries) {
            try {
                block()
            } catch (e: Exception) {
                Timber.e(e)
            }
            delay(delayMillis)
        }
        return block()
    }
}