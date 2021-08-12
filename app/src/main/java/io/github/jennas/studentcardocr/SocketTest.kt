package io.github.jennas.studentcardocr

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

fun main() {
    var socket: SocketTest = SocketTest(URI("ws://localhost:5000"))
    socket.connect()
}

class SocketTest : WebSocketClient {
    constructor(uri: URI) : super(uri)
    constructor(uri: URI, draft: Draft) : super(uri, draft)

    override fun onOpen(handshakedata: ServerHandshake?) {
        send("Hello, it is me. Mario :)")
        println("opened connection")
    }

    override fun onMessage(message: String?) {
        println("received: $message")
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        println(
            "Connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code +
                    " Reason: " + reason
        )
    }

    override fun onError(ex: Exception?) {
        ex!!.printStackTrace()
    }

//    fun main() {
//        var c: SocketTest = SocketTest(URI.create("ws://localhost:5000"))
//        c.connect()
//    }
}