package io.github.jennas.studentcardocr

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class Client(uri: URI, progress: ShowProgress) : WebSocketClient(uri) {
    var progress: ShowProgress? = null

    init {
        this.progress = progress
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.i("WebSocket", "OPEN")
    }

    override fun onMessage(message: String?) {
        if (message != null) {
            val jsonObject: JSONObject = JSONObject(message)

            this.progress!!.setProgress(jsonObject.getInt("progress"), jsonObject.getString("message"))

//            Log.i("WebSocket", message)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.i("WebSocket", "CLOSE")
    }

    override fun onError(e: Exception?) {
        Log.e("WebSocket Error!", e!!.printStackTrace().toString())
    }
}