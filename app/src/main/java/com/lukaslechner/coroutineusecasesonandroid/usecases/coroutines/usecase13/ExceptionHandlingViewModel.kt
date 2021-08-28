package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase13

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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

                val oreoFeatures = try {
                    oreoFeaturesDeferred.await()
                } catch (e: Exception) {
                    null
                }

                val pieFeatures = try {
                    pieFeaturesDeferred.await()
                } catch (e: Exception) {
                    null
                }

                val android10Features = try {
                    android10FeaturesDeferred.await()
                } catch (e: Exception) {
                    null
                }
                // list of not null only add the item not null
                val features = listOfNotNull(oreoFeatures, pieFeatures, android10Features)
                uiState.value = UiState.Success(versionFeatures = features)

            }
        }
    }
}