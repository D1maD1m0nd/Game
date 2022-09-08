package com.example.game.uiView.composeMafia

import android.content.Context
import android.util.DisplayMetrics
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.game.R
import com.example.game.uiView.data.Role
import com.example.game.uiView.composeMafia.viewModel.ViewModelMafiaRoomCompose
import com.example.game.webSocket.MessageListener
import com.example.game.webSocket.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MafiaRoomCompose(private val metrics: DisplayMetrics, private val context : Context) : MessageListener {

    private val serverUrl = "ws://192.168.0.102:8080/chat"

    init {
        WebSocketManager.init(serverUrl, this)

        CoroutineScope(Dispatchers.IO).launch {
            WebSocketManager.connect()
        }
    }

    private var role = Role.MAFIA
    private val viewModel : ViewModelMafiaRoomCompose = ViewModelMafiaRoomCompose()

    fun controlViewModel() {
        viewModel.loadingListPlayerAndRole()
        viewModel.showContent()
    }

    override fun onConnectSuccess()       { addText( "Connected successfully") }
    override fun onConnectFailed()        { addText( "Connection failed") }
    override fun onClose()                { addText( "Closed successfully") }
    override fun onMessage(text: String?) { addText( "$text") }

    private fun addText(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.setMess(text, R.drawable.avatar3, "Client")
        }
    }

    @Composable
    fun ContentMafia() {
        Image(painter = painterResource(id = R.drawable.back3), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        StateComposable()

        Row {
            Card(modifier = Modifier
                .width(45.dp)
                .height(25.dp)
                .clickable { viewModel.changeAndRepaintListPlayer() }
                .padding(start = 4.dp) , backgroundColor = Color.White) {}
            Card(modifier = Modifier
                .width(45.dp)
                .height(25.dp)
                .clickable { viewModel.setMess("Hello Chat " + Random().nextInt(1000), R.drawable.avatar2, "Dima") }
                .padding(start = 4.dp) , backgroundColor = Color.White) {}
        }
    }

    @Composable
    fun StateComposable() {
        if(viewModel.show.value) {
            ConstraintLayoutGame(viewModel.listPlayer, role, viewModel.listMess)
        }
    }

    @Composable
    fun ConstraintLayoutGame(listPlayer: List<ViewModelMafiaRoomCompose.PlayerState>, role: Role, listMess: List<ViewModelMafiaRoomCompose.Mess>) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (box) = createRefs()
            Box(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(box) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    },) {
                val angle = ((PI * 2) / listPlayer.size).toFloat()

                for (i in listPlayer.indices) {
                    CardPlayer(listPlayer[i], angle * i)
                }
                CardTable()
            }

            val (chatCard) = createRefs()
            Card(modifier = Modifier
                .width(pixToDp((2f * metrics.widthPixels) / 3f).dp)
                .height(260.dp)
                .constrainAs(chatCard) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },

                backgroundColor = Color.White,
                elevation = 5.dp,
                shape = RoundedCornerShape(30.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column {
                    Chat(listMess)
                }
            }

            var text by remember { mutableStateOf(TextFieldValue("")) }
            val (textField) = createRefs()
            Box(modifier = Modifier
                .padding(start = 0.dp, end = 0.dp, bottom = 0.dp)
                .height(54.dp)
                .width(pixToDp((2f * metrics.widthPixels) / 3f).dp)
                .constrainAs(textField) {
                    start.linkTo(parent.start)
                    end.linkTo(chatCard.end)
                    bottom.linkTo(parent.bottom)
                }) {
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    decorationBox = { innerTextField ->
                        Row(
                            Modifier
                                .background(Color(230, 230, 230), RoundedCornerShape(36.dp))
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            if (text.text.isEmpty()) {
                                Text("Введите ваше сообщение ...", color = Color(160,160,160), fontSize = 12.sp)
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 6.dp, end = 6.dp, bottom = 4.dp)
                )
            }

            val (nearMe) = createRefs()
            Image(painter = painterResource(id = R.drawable.ic_baseline_near_me_24), contentDescription = "", modifier = Modifier
                .clickable {
                    if (text.text != "") {
                        WebSocketManager.sendMessage(text.text)
                    }
                    text = TextFieldValue("")
                }
                .size(40.dp, 40.dp)
                .padding(end = 16.dp, bottom = 0.dp)
                .constrainAs(nearMe) {
                    end.linkTo(textField.end)
                    bottom.linkTo(textField.bottom)
                    top.linkTo(textField.top)
                }
            )

            val (textChat) = createRefs()
            Text(text = "Чат комнаты", color = Color(150,150,150), fontSize = 11.sp, modifier = Modifier
                .padding(end = 24.dp, top = 4.dp)
                .constrainAs(textChat) {
                    top.linkTo(chatCard.top)
                    end.linkTo(chatCard.end)
                })

            val (exit) = createRefs()
            Card(modifier = Modifier
                .height(54.dp)
                .width(pixToDp((metrics.widthPixels) / 3f).dp)
                .padding(start = 8.dp, bottom = 4.dp, end = 8.dp)
                .constrainAs(exit) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(chatCard.end)
                },
                shape = RoundedCornerShape(36.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                elevation = 2.dp,
                backgroundColor = Color(255,220,220)
            ) {

                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Выйти из комнаты", color = Color(105, 105, 105), fontSize = 11.sp)
                }
            }

            val (cardRole) = createRefs()
            Card(modifier = Modifier
                .width(pixToDp((metrics.widthPixels) / 3f).dp)
                .height(200.dp)
                .padding(start = 8.dp, bottom = 4.dp, end = 8.dp)
                .constrainAs(cardRole) {
                    bottom.linkTo(exit.top)
                    end.linkTo(parent.end)
                    start.linkTo(chatCard.end)
                    top.linkTo(chatCard.top)
                },

                backgroundColor = Color.White,
                elevation = 2.dp,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    when (role) {
                        Role.MAFIA -> {
                            Text(text = "Мафия", color = Color(255, 155, 155), fontSize = 20.sp)
                            Image(painter = painterResource(id = R.drawable.mafia), contentDescription = "")
                        }
                        Role.PEACE -> {
                            Text(text = "Мирный", color = Color(145, 220, 145), fontSize = 20.sp)
                            Image(painter = painterResource(id = R.drawable.peace), contentDescription = "")
                        }
                    }
                }
            }

        }
    }

    @Composable
    fun Chat(listMess: List<ViewModelMafiaRoomCompose.Mess>) {
        val listState = rememberLazyListState()

        LazyColumn( modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),state = listState) {
            items(listMess) { mess ->
                Mess(mess = mess)
            }
            CoroutineScope(Dispatchers.Main).launch {
                if(listMess.isNotEmpty()) {
                    listState.scrollToItem(listMess.size - 1)
                }
            }
        }
    }

    @Composable
    fun Mess(mess: ViewModelMafiaRoomCompose.Mess) {

        Row(modifier = Modifier.padding(start = 6.dp,top = 8.dp, end = 4.dp)) {
            Image(painter = painterResource(id = mess.drawable), contentDescription = "", modifier = Modifier
                .clip(CircleShape)
                .size(36.dp, 36.dp)
                .border(1.dp, Color.LightGray, CircleShape)
            )

            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(text = mess.name, color = Color(105, 105, 105), fontSize = 12.sp)
                Text(text = mess.mess, color = Color(145, 145, 145), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }

    @Composable
    fun CardPlayer(player: ViewModelMafiaRoomCompose.PlayerState, angle: Float) {
        var flagGame = false
        var alphaAvatar = 1f

        if(player.isDead.value) {
            flagGame = true
            alphaAvatar = 0.9f
        }

        val startValue = 0F
        val endValue = dpToPix(150).toFloat()
        val parameter = remember { Animatable(startValue) }

        LaunchedEffect(true) {
            parameter.animateTo(
                targetValue = endValue,
                animationSpec = tween(800, delayMillis = 300,easing = EaseOutCubic),
            )
        }

        Column(modifier = Modifier.offset(
            x = pixToDp(parameter.value * cos(angle) + metrics.widthPixels / 2 - dpToPix(55) / 2).dp,
            y = pixToDp(parameter.value * sin(angle) + metrics.heightPixels / 4 - dpToPix(55) / 2).dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier
                .width(55.dp)
                .height(55.dp)
                .clip(CircleShape)) {
                Image(
                    painter = painterResource(id = player.drawable),
                    alpha = alphaAvatar,
                    contentDescription = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                )
                if(flagGame) { CanvasDead() }
            }
            Text(text = player.name, color = Color.Black, fontSize = 10.sp, modifier = Modifier)
        }
    }

    @Composable
    fun CanvasDead() {
        val parameter = remember { Animatable(0f) }

        Image(
            painter = painterResource(id = R.drawable.dead),
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)
        )

        LaunchedEffect(true) {
            parameter.animateTo(
                targetValue = 200f,
                animationSpec = tween(200, easing = LinearEasing),
            )
        }

        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawLine(
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = parameter.value, y = parameter.value),
                color = Color(255, 0, 0),
                strokeWidth = 8F
            )

            drawLine(
                start = Offset(x = size.width, y = 0f),
                end = Offset(x = size.width - parameter.value, y = parameter.value),
                color = Color(255, 0, 0),
                strokeWidth = 8F
            )
        })
    }

    @Composable
    fun CardTable() {
        Card(modifier = Modifier
            .offset(
                x = pixToDp(metrics.widthPixels / 2f - dpToPix(200) / 2f).dp,
                y = pixToDp(metrics.heightPixels / 4f - dpToPix(200) / 2f).dp
            )
            .size(width = 200.dp, height = 200.dp),

            backgroundColor = Color(212, 248, 245),
            elevation = 5.dp,
            shape = RoundedCornerShape(200.dp),
            border = BorderStroke(1.dp, Color.White)
        ) {
            Image(painter = painterResource(id = R.drawable.wood), contentDescription = "", modifier = Modifier.fillMaxSize())
        }
    }

    private fun pixToDp(px: Float) = px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    private fun dpToPix(dp: Int) = (dp * context.resources.displayMetrics.density).toInt()
}