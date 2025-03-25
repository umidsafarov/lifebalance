package com.gmail.umidsafarov.lifebalance.extension

import androidx.navigation.NavHostController
import com.google.common.truth.Truth

fun NavHostController.assertCurrentRouteName(expectedRouteName: String) {
    Truth.assertThat(expectedRouteName).isEqualTo(currentBackStackEntry?.destination?.route)
}