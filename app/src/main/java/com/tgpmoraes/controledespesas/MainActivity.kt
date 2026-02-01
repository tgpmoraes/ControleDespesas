package com.tgpmoraes.controledespesas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tgpmoraes.controledespesas.ui.AppContent
import com.tgpmoraes.controledespesas.ui.theme.ControleDespesasTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ControleDespesasTheme {
                AppContent()
            }
        }
    }
}