package com.youplayertube.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.youplayertube.app.ui.AppViewModel
import com.youplayertube.app.ui.YouPlayerTubeApp

class MainActivity : ComponentActivity() {
    private val container get() = (application as YouPlayerTubeApplication).container
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val model: AppViewModel = viewModel(factory = viewModelFactory {
                initializer { AppViewModel(createSavedStateHandle(), container.library, container.preferences) }
            })
            YouPlayerTubeApp(model)
        }
    }
}
