package com.example.game.uiView.composeMafia.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.game.R
import com.example.game.webSocket.WebSocketManager
import java.util.*
import kotlin.collections.ArrayList

class ViewModelMafiaRoomCompose : InterfaceViewModelMafiaRoomCompose{

    data class Mess(val mess: String,val drawable : Int, val name : String)
    data class PlayerState(val name: String, val drawable: Int, var isDead: MutableState<Boolean>)

    private var _listMess = ArrayList<Mess>().toMutableStateList()
    val listMess : List<Mess> get() = _listMess

    private var _listPlayer = ArrayList<PlayerState>().toMutableStateList()
    val listPlayer : List<PlayerState> get() = _listPlayer

    val show = mutableStateOf(false)

    override fun showContent() { show.value = true }

    override fun changeAndRepaintListPlayer() {
        _listPlayer[Random().nextInt(11)].isDead.value = true
    }

    override fun loadingListPlayerAndRole() {
        listPlayerEx.forEach { e ->
            _listPlayer.add(PlayerState(e.name,e.drawable, mutableStateOf(e.isDead)))
        }
    }

    override fun setMess(mess : String, drawable : Int, name: String) {
       _listMess.add(Mess(mess,drawable,name))
    }

    data class Player(val name: String, val drawable: Int, var isDead: Boolean)
    private var listPlayerEx = listOf(
        Player("Nikita", R.drawable.avatar2,false),
        Player("Viktor",R.drawable.avatar2,false),
        Player("Viktor",R.drawable.avatar2,false),
        Player("Katua",R.drawable.avatar1,false),
        Player("Katua",R.drawable.avatar1,false),
        Player("Masha",R.drawable.avatar3,false),
        Player("Dima",R.drawable.avatar2,false),
        Player("Dima",R.drawable.avatar2,false),
        Player("Lena",R.drawable.avatar3,false),
        Player("Lena",R.drawable.avatar3,false),
        Player("Vika",R.drawable.avatar1,false)
    )
}

