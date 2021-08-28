package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase13

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.*

class ExceptionHandlingViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun handleExceptionWithTryCatch() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                api.getAndroidVersionFeatures(27)
            } catch (e: Exception) {
                uiState.value = UiState.Error("Network Request failed: $e")
            }
        }
    }

    fun handleWithCoroutineExceptionHandler() {
        uiState.value = UiState.Loading
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            uiState.value = UiState.Error("Network request failed!")
        }
        viewModelScope.launch(exceptionHandler) {
            api.getAndroidVersionFeatures(27)
        }

    }

    fun showResultsEvenIfChildCoroutineFails() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            // we have to add supervisor scope to make sure
            // one job failed it will not be propagated
            supervisorScope {
                val oreoFeaturesDeferred = async {
                    api.getAndroidVersionFeatures(27)
                }
                val pieFeaturesDeferred = async {
                    api.getAndroidVersionFeatures(28)
                }
                val android10FeaturesDeferred = async {
                    api.getAndroidVersionFeatures(29)
                }

                val features = listOf(
                    oreoFeaturesDeferred,
                    pieFeaturesDeferred,
                    android10FeaturesDeferred
                ).mapNotNull {
                    try {
                        it.await()
                    } catch (e: java.lang.Exception) {
                        if (e is CancellationException) {
                            // if we cancel we want to complete immediately
                            throw e
                        }
                        null
                    }
                }

                uiState.value = UiState.Success(versionFeatures = features)

            }
        }
    }
}