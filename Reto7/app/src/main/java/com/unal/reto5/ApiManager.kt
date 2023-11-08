package com.unal.reto5

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class ApiManager (val context: Context) {

    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    fun obtenerJuegosDisponibles(url: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                onSuccess(response)
            },
            { error ->
                onError(error.message ?: "Error desconocido")
            })

        requestQueue.add(stringRequest)
    }
}