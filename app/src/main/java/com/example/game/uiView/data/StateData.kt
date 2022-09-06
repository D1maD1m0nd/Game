package com.example.game.uiView.data

import com.example.game.uiView.composeMafia.viewModel.ViewModelMafiaRoomCompose

sealed class StateData {
    data class Show(val list: List<ViewModelMafiaRoomCompose.Player>, val role: Role) : StateData()
    data class Repaint(val list : List<ViewModelMafiaRoomCompose.Player>, val countRepaint : Int) : StateData()
    data class UpdateChat(val listMess : ArrayList<ViewModelMafiaRoomCompose.Mess>, val countRepaintMess : Int) : StateData()
    data class Error(val error : Throwable)  : StateData()
}
