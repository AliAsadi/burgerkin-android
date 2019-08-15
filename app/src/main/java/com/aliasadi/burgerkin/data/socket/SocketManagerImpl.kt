package com.aliasadi.burgerkin.data.socket

import android.util.Log
import com.aliasadi.burgerkin.data.model.Account
import com.aliasadi.burgerkin.data.model.Player
import com.google.gson.Gson
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import org.json.JSONArray
import com.google.gson.reflect.TypeToken
import android.os.Handler
import android.os.Looper


class SocketManagerImpl private constructor(account: Account) : SocketManager {

    val handler = Handler(Looper.getMainLooper())

    val TAG = "MainActivity"

    val EVENT_TURN = "turn"
    val EVENT_FLIP = "flip"
    val EVENT_WIN = "win"
    val EVENT_JOIN = "join"
    val EVENT_ACTION = "action"
    val EVENT_RESULT = "result"

    private val socket: Socket

    private var listener: SocketManager.ActionsListener? = null

    private val onConnect = Emitter.Listener {
        Log.d(TAG, "onConnect")
    }

    private val onDisconnect = Emitter.Listener {
        Log.d(TAG, "onDisconnect")
    }

    private val onAction = Emitter.Listener {

        val jsonObject = it[0] as JSONObject
        val action = jsonObject["action"] as String

        Log.d(TAG, "$action -> $jsonObject")

        handler.post {
            when (action) {
                EVENT_TURN -> {
                    val publicAddress = jsonObject["value"] as String
                    listener?.onSwitchTurn(publicAddress)
                }

                EVENT_FLIP -> {
                    val value = jsonObject["value"] as JSONObject
                    val result = value["result"] as JSONObject
                    listener?.onCardFlipped(result["position"] as Int, result["symbol"].toString())
                }

                EVENT_WIN -> {
                    val winnerPublicAddress = jsonObject["value"] as String
                    listener?.onFinishGame(winnerPublicAddress)
                }

                EVENT_JOIN -> {
                    val value = jsonObject["value"] as JSONObject

                    if (value["state"] == "starting" || value["state"] == "playing") {

                        val playersMap: HashMap<String, Player> = Gson().fromJson(
                            value["players"].toString(),
                            object : TypeToken<HashMap<String, Player>>() {}.type
                        )

                        val items = jsonArray2IntArray(value["board"] as JSONArray)
                        val players = playersMap.values.toTypedArray()


                        handler.post {
                            listener?.onStartGame(items, players)
                        }
                    }

                }

                EVENT_RESULT -> {
                    val value = jsonObject["value"] as JSONObject

                    val match = value["match"] as Boolean
                    val positions = jsonArray2IntArray(value["positions"] as JSONArray)

                    listener?.onMatch(match, positions)
                }

            }

        }

    }


    init {
        val options = IO.Options()
        options.query = "token=${account.publicAddress}&name=${account.name}"

        socket = IO.socket("https://kinburger.herokuapp.com/", options)

        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        socket.on(EVENT_ACTION, onAction)
    }

    private fun jsonArray2IntArray(jsonArray: JSONArray): IntArray {
        val intArray = IntArray(jsonArray.length())
        for (i in intArray.indices) {
            intArray[i] = if (jsonArray.isNull(i)) 999 else jsonArray.optInt(i)
        }
        return intArray
    }

    override fun connect() {
        socket.connect()
    }

    override fun disconnect() {
        socket.disconnect()
    }

    override fun setListener(listener: SocketManager.ActionsListener) {
        this.listener = listener
    }


    override fun sendFlipEvent(position: Int) {
        socket.emit("action", "flip", position, Ack {
            Log.d(TAG, "flip callback -> " + it[0])
            val json = it[0] as JSONObject

            if (json.has("error")) {
                val error = json["error"] as JSONObject
                handler.post {
                    listener?.onError(error["description"] as String)
                }
            }

        })
    }

    companion object {

        private var singleton: SocketManager? = null

        fun init(account: Account) {
            if (singleton == null) {
                singleton = SocketManagerImpl(account)
            }
        }

        fun getInstance(): SocketManager {
            return singleton!!
        }
    }


}
