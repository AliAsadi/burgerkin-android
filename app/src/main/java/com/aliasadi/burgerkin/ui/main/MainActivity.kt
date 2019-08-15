package com.aliasadi.burgerkin.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliasadi.burgerkin.*
import com.aliasadi.burgerkin.data.model.Player
import com.aliasadi.burgerkin.data.socket.SocketManagerImpl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.board_view.*
import kotlinx.android.synthetic.main.player_view.view.*


class MainActivity : AppCompatActivity(), CardAdapter.CardListener,
    MainContract.View {

    private val TAG = this.javaClass.simpleName

    private lateinit var adapter: CardAdapter
    private lateinit var presenter: MainPresenter

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, MainActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val socketManager = SocketManagerImpl.getInstance()

        adapter = CardAdapter(this)
        recyclerView.layoutManager =
            GridLayoutManager(this, 4, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        presenter = MainPresenter(this, socketManager)

        startGameButton.setOnClickListener {
            presenter.onStartGameClicked()
        }
    }

    override fun flipCardsToBack(positions: IntArray) {
        adapter.flipCardsToBack(positions)
    }

    override fun removeCardsFromBoard(positions: IntArray) {
        adapter.removeCardsFromBoard(positions)
    }

    override fun showGameScreen() {
        startGameButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun switchTurn(isMyTurn: Boolean) {
        val rotationAngle = if (isMyTurn) 0.0f else 180.0f
        arrowImage.animate().rotation(rotationAngle).setDuration(500).start()
    }


    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun flipCardToFront(cardPosition: Int, cardName: String) {
        adapter.flipCardToFront(cardPosition, cardName)
        adapter.notifyItemChanged(cardPosition)
        Log.d(TAG, "flipCardToFront")
    }

    override fun startGame(items: IntArray, myPlayer: Player, secPlayer: Player) {
        adapter.setItems(items)
        firstPlayer.name.text = myPlayer.name
        secondPlayer.name.text = secPlayer.name

        progressBar.visibility = View.GONE
        boardView.visibility = View.VISIBLE
    }

    override fun onClick(cardPosition: Int) {
        presenter.onCardClicked(cardPosition)
    }
}
