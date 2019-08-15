package com.aliasadi.burgerkin.data.socket

import com.aliasadi.burgerkin.data.model.Player

interface SocketManager {
    fun connect()
    fun disconnect()

    fun setListener(listener: ActionsListener)
    fun sendFlipEvent(position: Int)

    interface ActionsListener {
        fun onSwitchTurn(publicAddress: String)
        fun onPlayerJoin(name: String)
        fun onCardFlipped(position: Int, cardName: String)
        fun onFinishGame(winnerPublicAddress: String)
        fun onError(message: String)
        fun onMatch(match: Boolean, positions: IntArray)
        fun onStartGame(items: IntArray, players: Array<Player>)
    }

}
