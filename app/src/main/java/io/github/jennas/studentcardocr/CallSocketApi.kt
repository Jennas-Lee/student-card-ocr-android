package io.github.jennas.studentcardocr

import android.util.Log
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket

class CallSocketApi {
    private lateinit var socket: Socket

    fun connect() {
        try {
            socket = Socket("http://10.0.2.2", 5000)
        } catch (e: Exception) {
            Log.e("Socket Connection Error : ", e.printStackTrace().toString())
        }
    }

    fun disconnect() {
        try {
            socket.close()
        } catch (e: Exception) {
            Log.e("Socket Disconnection Error : ", e.toString())
        }
    }

    fun send(message: String) {
        try {
            PrintWriter(socket.getOutputStream(), true).println(message)
        } catch (e: Exception) {
            Log.e("Socket Send Error : ", e.toString())
        }
    }
}