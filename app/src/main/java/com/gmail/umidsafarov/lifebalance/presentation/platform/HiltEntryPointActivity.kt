package com.gmail.umidsafarov.lifebalance.presentation.platform

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/*
Created to workaround about an issue related to Hilt and instrumented tests
Issue: https://github.com/google/dagger/issues/3394
 */

@AndroidEntryPoint
open class HiltEntryPointActivity : ComponentActivity()