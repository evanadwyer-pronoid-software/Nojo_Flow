package com.pronoidsoftware.nojoflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pronoidsoftware.nojoflow.presentation.editnote.EditNoteScreenRoot
import com.pronoidsoftware.nojoflow.presentation.ui.theme.NojoFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NojoFlowTheme {
                EditNoteScreenRoot()
            }
        }
    }
}
