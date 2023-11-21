package com.unal.reto10

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class ApiRequestManager (private val context: Context) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    fun fetchDepartments(callback: (List<String>) -> Unit) {
        val url = "https://www.datos.gov.co/resource/57sv-p2fu.json"
        val departments = HashSet<String>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val department = jsonObject.getString("departamento")
                    departments.add(department)
                }
                val sortedDepartments = departments.toList().sorted()
                callback.invoke(sortedDepartments)
            },
            { error ->
                Log.e("ApiRequestManager", "Error: ${error.message}")
                callback.invoke(emptyList())
            })

        requestQueue.add(jsonArrayRequest)
    }

    fun fetchSensorsTypes(callback: (List<String>) -> Unit) {
        val url = "https://www.datos.gov.co/resource/57sv-p2fu.json"
        val sensors = HashSet<String>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val description = jsonObject.getString("descripcionsensor")
                    sensors.add(description)
                }
                val uniqueSensors = sensors.toList().distinct()
                callback.invoke(uniqueSensors)
            },
            { error ->
                Log.e("ApiRequestManager", "Error: ${error.message}")
                callback.invoke(emptyList())
            })

        requestQueue.add(jsonArrayRequest)
    }

    fun fetchAllRecordsForDepartment(department: String, callback: (List<String>) -> Unit) {
        val url = "https://www.datos.gov.co/resource/57sv-p2fu.json?departamento=$department"
        val records = mutableListOf<String>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    records.add(jsonObject.toString()) // Agrega cada registro completo como un String
                }
                callback.invoke(records)
            },
            { error ->
                Log.e("ApiRequestManager", "Error: ${error.message}")
                callback.invoke(emptyList())
            })

        requestQueue.add(jsonArrayRequest)
    }

    fun fetchSensorsByType(sensorType: String, limit: Int, callback: (List<String>) -> Unit) {
        val url = "https://www.datos.gov.co/resource/57sv-p2fu.json?descripcionsensor=$sensorType"

        val sensors = mutableListOf<String>()
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                for (i in 0 until minOf(response.length(), limit)) {
                    val jsonObject = response.getJSONObject(i)
                    sensors.add(jsonObject.toString())
                }
                callback.invoke(sensors)
            },
            { error ->
                Log.e("ApiRequestManager", "Error: ${error.message}")
                callback.invoke(emptyList())
            }
        )

        requestQueue.add(jsonArrayRequest)
    }


}