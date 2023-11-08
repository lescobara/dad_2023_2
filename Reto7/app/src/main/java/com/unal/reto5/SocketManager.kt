package com.unal.reto5

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketManager {

    private lateinit var socketObj: Socket

    @Synchronized
    fun setSocket(){
        try {
            //socketObj = IO.socket("http://192.168.20.10:3000")
            socketObj = IO.socket("http://10.0.2.2:3000")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Log.e("SOCKET!!!", "Error en la URL: ${e.message}")
        }
    }

    @Synchronized
    fun getSocket():Socket{
        return socketObj
    }

    @Synchronized
    fun establishConnection(){
        socketObj.connect()
        Log.e("SOCKET!!!", "Conectado!")
    }

    @Synchronized
    fun closeConnection(){
        socketObj.disconnect()
        Log.e("SOCKET!!!", "Desconectado!")
    }

}