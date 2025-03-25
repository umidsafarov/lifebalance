package com.gmail.umidsafarov.lifebalance.presentation.ui.uicomponent.popup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun SnackbarMessage(snackbarHostState: SnackbarHostState) {
    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { Snackbar(it) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}