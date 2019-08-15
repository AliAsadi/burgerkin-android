package com.aliasadi.burgerkin.ui.main

import com.aliasadi.burgerkin.data.model.Player

interface MainContract {

    interface View {
        fun switchTurn(isMyTurn: Boolean)
        fun flipCardToFront(cardPosition: Int, cardName: String)
        fun showMessage(message: String)
        fun showGameScreen()
        fun flipCardsToBack(positions: IntArray)
        fun startGame(items: IntArray, myPlayer: Player, secPlayer: Player)
        fun removeCardsFromBoard(positions: IntArray)
    }

    interface Presenter {
        fun onCardClicked(cardPosition: Int)
    }

}
