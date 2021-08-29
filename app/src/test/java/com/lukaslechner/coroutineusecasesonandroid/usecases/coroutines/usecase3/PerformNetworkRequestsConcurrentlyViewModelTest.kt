package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesOreo
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesPie
import com.lukaslechner.coroutineusecasesonandroid.utils.MainCoroutineScopeRule
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class PerformNetworkRequestsConcurrentlyViewModelTest {


    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val receivedUiState = mutableListOf<UiState>()

    @get: Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    // when using run blocking test we need to make sure its scope is the same as our custom rule
    @Test
    fun `performNetworkRequestsSequentially should load data sequentially`() = mainCoroutineScopeRule.runBlockingTest {
        //Arrange
        val responseDelay = 1000L
        val fakeApi = FakeSuccessApi(responseDelay)
        val viewmodel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)

        //there is no lifecycle in junit test
        observeViewModel(viewmodel)

        //Act
        viewmodel.performNetworkRequestsSequentially()
        val forwardTime = advanceUntilIdle() // it will advance all delay

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(
                    listOf(
                        mockVersionFeaturesOreo,
                        mockVersionFeaturesPie,
                        mockVersionFeaturesAndroid10
                    )
                )
            ),
            receivedUiState
        )

        Assert.assertEquals(3000, forwardTime)
    }

    @Test
    fun `performNetworkRequestsConcurrently should load data concurrently`() = mainCoroutineScopeRule.runBlockingTest {
        //Arrange
        val responseDelay = 1000L
        val fakeApi = FakeSuccessApi(responseDelay)
        val viewmodel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)

        //there is no lifecycle in junit test
        observeViewModel(viewmodel)

        //Act
        viewmodel.performNetworkRequestsConcurrently()
        val forwardTime = advanceUntilIdle() // it will advance all delay

        //Assert
        Assert.assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(
                    listOf(
                        mockVersionFeaturesOreo,
                        mockVersionFeaturesPie,
                        mockVersionFeaturesAndroid10
                    )
                )
            ),
            receivedUiState
        )

        Assert.assertEquals(1000, forwardTime)

    }

    private fun observeViewModel(viewmodel: PerformNetworkRequestsConcurrentlyViewModel) {
        viewmodel.uiState().observeForever { uiState ->
            if (uiState != null) {
                receivedUiState.add(uiState)
            }
        }
    }


}