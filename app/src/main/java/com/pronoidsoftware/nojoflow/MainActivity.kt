package com.pronoidsoftware.nojoflow

import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.pronoidsoftware.nojoflow.presentation.NavigationRoot
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.insetsController?.hide(WindowInsets.Type.statusBars())
        setContent {
            NojoFlowTheme {
                val navHostController = rememberNavController()
                NavigationRoot(navHostController)
            }
        }
    }
}
