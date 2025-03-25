package com.gmail.umidsafarov.lifebalance.extension

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> TestScope.collectInBackground(flow: Flow<T>): MutableList<T> {
    val list = mutableListOf<T>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        flow.toList(list)
    }
    return list
}