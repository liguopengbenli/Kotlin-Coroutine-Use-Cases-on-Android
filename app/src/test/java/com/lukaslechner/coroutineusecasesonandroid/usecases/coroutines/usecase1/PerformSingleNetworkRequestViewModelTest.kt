package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import com.lukaslechner.coroutineusecasesonandroid.utils.MainCoroutineScopeRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.rules.TestRule

class PerformSingleNetworkRequestViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val receivedUiState = mutableListOf<UiState>()

    @get: Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()


    //kotlin feature test name ``
    @Test
    fun `should return Success when network request is successful`() {
        //Arrange
        val fakeApi = FakeSuccessApi()
        val viewmodel = PerformSingleNetworkRequestViewModel(fakeApi)

        //there is no lifecycle in junit test
        observeViewModel(viewmodel)

        //Act
        viewmodel.performSingleNetworkRequest()

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(mockAndroidVersions)
            ),
            receivedUiState
        )
    }

    private fun observeViewModel(viewmodel: PerformSingleNetworkRequestViewModel) {
        viewmodel.uiState().observeForever {
            receivedUiState.add(it)
        }
    }


    @Test
    fun `should return Error when network request failed`() {
        // Arrange
        val fakeAPi = FakeErrorApi()
        val viewModel = PerformSingleNetworkRequestViewModel(fakeAPi)
        observeViewModel(viewModel)

        // Act
        viewModel.performSingleNetworkRequest()

        // Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error(viewModel.networkRequestFailedMessage)
            ),
            receivedUiState
        )
    }

}