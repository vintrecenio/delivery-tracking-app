package com.treceniovin.deliverytracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.treceniovin.deliverytracking.ui.navigation.NavGraph
import com.treceniovin.deliverytracking.ui.theme.DeliveryTrackingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeliveryTrackingTheme {
                NavGraph()
            }
        }
    }
}
