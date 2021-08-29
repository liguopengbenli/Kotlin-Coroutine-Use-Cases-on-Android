package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.utils.MainCoroutineScopeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class Perform2SequentialNetworkRequestsViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val receivedUiState = mutableListOf<UiState>()

    @get: Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun `should return Success when network request is successful`() {
        val fakeApi = FakeSequentialApi()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(fakeApi)

        observeViewModel(viewModel)

        // Act
        viewModel.perform2SequentialNetworkRequest()

        // Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(mockVersionFeaturesAndroid10)
            ),
            receivedUiState
        )
    }

    @Test
    fun `should return failed when network version request failed`() {
        val fakeApi = FakeVersionErrorSequentialApi()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(fakeApi)

        observeViewModel(viewModel)

        // Act
        viewModel.perform2SequentialNetworkRequest()

        // Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error(viewModel.networkRequestFailedMessage)
            ),
            receivedUiState
        )
    }

    @Test
    fun `should return failed when network features request failed`() {
        val fakeApi = FakeFeaturesErrorSequentialApi()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(fakeApi)

        observeViewModel(viewModel)

        // Act
        viewModel.perform2SequentialNetworkRequest()

        // Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error(viewModel.networkRequestFailedMessage)
            ),
            receivedUiState
        )
    }

    private fun observeViewModel(viewmodel: Perform2SequentialNetworkRequestsViewModel) {
        viewmodel.uiState().observeForever {
            receivedUiState.add(it)
        }
    }
}