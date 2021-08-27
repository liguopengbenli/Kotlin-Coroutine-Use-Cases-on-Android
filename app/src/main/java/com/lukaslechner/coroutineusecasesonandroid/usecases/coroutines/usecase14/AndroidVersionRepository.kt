package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase14

import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AndroidVersionRepository(
    private var database: AndroidVersionDao,
    private val scope: CoroutineScope,
    private val api: MockApi = mockApi()
) {

    suspend fun getLocalAndroidVersions(): List<AndroidVersion> {
        return database.getAndroidVersions().mapToUiModelList()
    }

    // using custom scope to ensure the execution of task even user leave the screen
    // here is in ViewmodelScope and we switch to custom scope
    suspend fun loadAndStoreRemoteAndroidVersions(): List<AndroidVersion> {

        return scope.async {
            val recentVersions = api.getRecentAndroidVersions()
            for (recentVersion in recentVersions) {
                database.insert(recentVersion.mapToEntity())
            }
            recentVersions
        }.await()

    }

    fun clearDatabase() {
        scope.launch {
            database.clear()
        }
    }
}