package com.example.game.uiView.composeMafia.viewModel

interface InterfaceViewModelMafiaRoomCompose {
    fun loadingListPlayerAndRole()
    fun showContent()
    fun setMess(mess : String, drawable : Int, name: String)
    fun changeAndRepaintListPlayer()
}