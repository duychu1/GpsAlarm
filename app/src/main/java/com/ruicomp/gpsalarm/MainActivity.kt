package com.ruicomp.gpsalarm

import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ruicomp.gpsalarm.feature.home.HomeScreen
import com.ruicomp.gpsalarm.navigation.MyNavHost
import com.ruicomp.gpsalarm.ui.theme.TemplateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TemplateTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) {
                            Snackbar(snackbarData = it, containerColor = androidx.compose.ui.graphics.Color.Green)
                        }
                    },
                ) { innerPadding ->
                    MyNavHost(
                        modifier = Modifier.padding(innerPadding),
                        snackbarHost = snackbarHostState
                    )
                }
            }
        }
    }
}

