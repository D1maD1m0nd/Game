package com.example.game.uiView.activity

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.game.ui.theme.GameTheme
import com.example.game.uiView.composeMafia.MafiaRoomCompose
import com.example.game.webSocket.WebSocketManager
import com.example.game.webSocket.MessageListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(){

     private val metrics by lazy {
         val metrics = DisplayMetrics()
         this.windowManager.defaultDisplay.getMetrics(metrics)
         metrics
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mafiaRoomCompose = MafiaRoomCompose(metrics,this)

        setContent {
            GameTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    mafiaRoomCompose.ContentMafia()
                }
            }
        }

        mafiaRoomCompose.controlViewModel()
    }
}
