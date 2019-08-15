package com.aliasadi.burgerkin.ui.main

import android.util.Log
import com.aliasadi.burgerkin.App
import com.aliasadi.burgerkin.data.model.Player
import com.aliasadi.burgerkin.data.socket.SocketManager

class MainPresenter(
    private val view: MainContract.View,
    private val socketManager: SocketManager
) : MainContract.Presenter {


    init {
        socketManager.setListener(object : SocketManager.ActionsListener {
            override fun onStartGame(items: IntArray, players: Array<Player>) {

                val isMeFirstPlayer = players[0].id == App.instance.getAccount().publicAddress

                val myPlayer = if (isMeFirstPlayer) players[0] else players[1]
                val secPlayer = if (isMeFirstPlayer) players[1] else players[0]

                view.startGame(items, myPlayer, secPlayer)
            }

            override fun onMatch(match: Boolean, positions: IntArray) {
                if (match) {
                    view.removeCardsFromBoard(positions)
                } else {
                    view.flipCardsToBack(positions)
                }
            }

            override fun onError(message: String) {
                Log.d(TAG, "onError: $message")
                view.showMessage(message)
            }

            override fun onSwitchTurn(publicAddress: String) {

                val isMyTurn = publicAddress == App.instance.getAccount().publicAddress
                view.switchTurn(isMyTurn)
                Log.d(TAG, "onSwitchTurn: $publicAddress")
            }

            override fun onPlayerJoin(name: String) {
                Log.d(TAG, "onPlayerJoin: $name")
            }

            override fun onCardFlipped(position: Int, cardName: String) {
                Log.d(TAG, "onCardFlipped: $cardName")

                view.flipCardToFront(position, cardName)
            }

            override fun onFinishGame(winnerPublicAddress: String) {
                Log.d(TAG, "winner: $winnerPublicAddress")

                view.showMessage("Finish!")

            }

        })
    }

    override fun onCardClicked(cardPosition: Int) {
        socketManager.sendFlipEvent(cardPosition)
    }

    fun onStartGameClicked() {
        socketManager.connect()
        view.showGameScreen()
    }

    companion object {
        private val TAG = "MainActivity"
    }
}
