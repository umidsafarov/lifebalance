package com.gmail.umidsafarov.lifebalance.presentation.navigation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.gmail.umidsafarov.lifebalance.presentation.platform.HiltEntryPointActivity
import com.gmail.umidsafarov.lifebalance.presentation.ui.theme.LifeBalanceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationActivity : HiltEntryPointActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeBalanceTheme {
                Navigation(navController = rememberNavController())
            }
        }
    }
}
