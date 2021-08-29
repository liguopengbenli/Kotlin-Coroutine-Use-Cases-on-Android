package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import com.lukaslechner.coroutineusecasesonandroid.mock.*
import java.net.UnknownHostException
import java.net.UnknownServiceException

class FakeFeaturesErrorSequentialApi : MockApi {
    override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
        return mockAndroidVersions
    }

    override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures =  when (apiLevel) {
        27 -> mockVersionFeaturesOreo
        28 -> mockVersionFeaturesPie
        29 -> throw UnknownServiceException()
        else -> throw UnknownHostException()
    }

}
