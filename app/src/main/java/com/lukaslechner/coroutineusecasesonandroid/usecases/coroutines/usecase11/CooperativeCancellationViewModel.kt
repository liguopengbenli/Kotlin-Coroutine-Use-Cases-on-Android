package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import timber.log.Timber
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CooperativeCancellationViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private var calculationJob: Job? = null

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading
        calculationJob = viewModelScope.launch {
            try {
                var result: BigInteger = BigInteger.ZERO
                val computationDuration = measureTimeMillis {
                    result = calculateFactorialOf(factorialOf)
                }

                var resultString = ""
                val stringConversionDuration = measureTimeMillis {
                    resultString = convertToString(result)
                }

                uiState.value =
                    UiState.Success(resultString, computationDuration, stringConversionDuration)
            } catch (exception: CancellationException) {
                uiState.value = UiState.Error("the job is cancelled!")
            } catch (exception: Exception) {
                uiState.value = UiState.Error("Error while calculating result")
            }
        }
        calculationJob?.invokeOnCompletion { throwable->
            if (throwable is CancellationException) {
                Timber.d("Calculation was cancelled!")
            }

        }
    }

    fun cancelCalculation() {
        calculationJob?.cancel()
    }

    // factorial of n (n!) = 1 * 2 * 3 * 4 * ... * n
    private suspend fun calculateFactorialOf(number: Int): BigInteger =
        withContext(defaultDispatcher) {
            var factorial = BigInteger.ONE
            for (i in 1..number) {
                yield()
                factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            }
            Timber.d("Calculation completed")
            factorial
        }

    private suspend fun convertToString(number: BigInteger): String =
        withContext(defaultDispatcher) {
            number.toString()
        }

    fun uiState(): LiveData<UiState> = uiState

    private val uiState: MutableLiveData<UiState> = MutableLiveData()
}