package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase4

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class VariableAmountOfNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val recentVersion = mockApi.getRecentAndroidVersions()
                val versionsFeatures = recentVersion.map { androidVersion ->
                    mockApi.getAndroidVersionFeatures(androidVersion.apiLevel)
                }
                uiState.value = UiState.Success(versionsFeatures)
            } catch (e: Exception) {
                uiState.value = UiState.Error("$e")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading
        val features = viewModelScope.launch {
            try {
                val versions = mockApi.getRecentAndroidVersions()
                // map to get a collection of deferred and await all get the list of result
                val features = versions.map { androidVersion ->
                    async {
                        mockApi.getAndroidVersionFeatures(androidVersion.apiLevel)
                    }
                }.awaitAll()
                uiState.value = UiState.Success(features)
            } catch (e: java.lang.Exception) {
                uiState.value = UiState.Error("$e")
            }

        }

    }
}