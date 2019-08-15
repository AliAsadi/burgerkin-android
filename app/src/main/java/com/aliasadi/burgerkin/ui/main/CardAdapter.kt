package com.aliasadi.burgerkin.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aliasadi.burgerkin.App
import com.aliasadi.burgerkin.R
import kotlinx.android.synthetic.main.card_item.view.*

class CardAdapter(
    private val listener: CardListener
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>(), View.OnClickListener {

    interface CardListener {
        fun onClick(cardPosition: Int)
    }

    private var items: IntArray = IntArray(16) { 0 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardImage = holder.itemView.cardImage
        cardImage.setImageResource(getCardImageRecourse(position))
        cardImage.tag = position
        cardImage.setOnClickListener(this)
    }

    fun setItems(items: IntArray) {
        this.items = items
        notifyDataSetChanged()
    }

    private fun getCardImageRecourse(position: Int): Int {
        val imageName = items[position]
        return App.instance.resources.getIdentifier("card$imageName", "drawable", App.instance.packageName)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onClick(view: View) {
        val position = view.tag as Int
        listener.onClick(position)
    }


    fun flipCardsToBack(positions: IntArray) {
        positions.forEach {
            items[it] = 0
            notifyItemChanged(it)
        }
    }

    fun flipCardToFront(cardPosition: Int, cardName: String) {
        items[cardPosition] = cardName.toInt()
        notifyItemChanged(cardPosition)
    }

    fun removeCardsFromBoard(positions: IntArray) {
        positions.forEach {
            items[it] = 999
            notifyItemChanged(it)
        }
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private val TAG = "MainActivity"
    }
}
