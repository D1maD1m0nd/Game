package com.example.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.game.ui.theme.GameTheme
import com.example.web_socketsample.MessageListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), MessageListener {
    private val serverUrl = "ws://192.168.0.104:8080/chat"
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebSocketManager.init(serverUrl, this)
        setContent {
            GameTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        Row {
                            ConnectButton()
                            ReconnectButton()
                            SendMessageButton()
                        }
                        SimpleOutlinedTextFieldSample()
                        MessageList(viewModel.list)
                    }
                }
            }
        }
    }
    @Composable
    fun ConnectButton() {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                WebSocketManager.connect()
            }
        }){
            Text("Connect", fontSize = 25.sp)
        }
    }

    @Composable
    fun SendMessageButton() {
        Button(onClick = {
            WebSocketManager.sendMessage( viewModel.textLiveData.value ?: "")
        }){
            Text("Send", fontSize = 25.sp)
        }
    }

    @Composable
    fun ReconnectButton() {
        Button(onClick = {}){
            Text("Reconnect", fontSize = 25.sp)
        }
    }
    @Composable
    fun SimpleOutlinedTextFieldSample() {
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            onValueChange = {
                viewModel.setMessage(it)
                text = it
            },
            label = { Text("Label") }
        )
    }

    @Composable
    fun MessageList(list: List<String>?) {
        LazyColumn {
            items(items = list ?: listOf()) { item ->
                Text(item)
            }
        }
    }
    override fun onConnectSuccess() {
        addText( " Connected successfully \n " )
    }

    override fun onConnectFailed() {
        addText( " Connection failed \n " )
    }

    override fun onClose() {
        addText( " Closed successfully \n " )
    }

    override fun onMessage(text: String?) {
        addText( " Receive message: $text \n " )
    }

    private fun addText(text: String) {
        viewModel.addMessage(text)
    }
}
