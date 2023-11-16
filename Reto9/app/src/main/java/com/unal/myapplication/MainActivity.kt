package com.unal.myapplication

/*import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración de OpenStreetMap
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        // Inicialización del MapView
        mapView = findViewById(R.id.mapView)
        mapView.setBuiltInZoomControls(true)
        mapView.controller.setZoom(15.0)

        // Obtener ubicación actual y mostrar en el mapa
        val userLocation = GeoPoint(4.637742, -74.084440)  // Ejemplo: UN
        mapView.controller.setCenter(userLocation)

        // Agregar marcador de ubicación actual
        val marker = Marker(mapView)
        marker.position = userLocation
        marker.title = "Ubicación Actual"
        mapView.overlays.add(marker)

    }
}*/

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.osmdroid.views.overlay.FolderOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestQueue: RequestQueue
    // Declarar la variable de marcador a nivel de clase
    private lateinit var userLocationMarker: Marker
    private lateinit var userLocation: GeoPoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar la barra de herramientas solo si el tema no incluye una "action bar"
        if (supportActionBar == null) {
            setSupportActionBar(findViewById(R.id.toolbar))
        }

        // Configuración de OpenStreetMap
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        // Inicialización del MapView
        mapView = findViewById(R.id.mapView)
        mapView.setBuiltInZoomControls(true)
        mapView.controller.setZoom(15.0)

        // Inicialización de FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Obtener ubicación actual y mostrar en el mapa
            getLastLocationAndShowOnMap()
        } else {
            // Solicitar permisos de ubicación
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_app, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                // Lógica para la acción "Buscar"
                showSearchDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showSearchDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)
        val editTextRadius = dialogView.findViewById<EditText>(R.id.editTextRadius)
        val spinnerPoiType = dialogView.findViewById<Spinner>(R.id.spinnerPoiType)
        val btnSearch = dialogView.findViewById<Button>(R.id.btnSearch)

        // Configurar el adaptador para el Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.poi_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPoiType.adapter = adapter

        // Establecer el valor predeterminado del campo de radio
        editTextRadius.setText("100")

        // Crear y mostrar el diálogo
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Buscar Puntos de Interés")
            .create()

        btnSearch.setOnClickListener {
            val radiusText = editTextRadius.text.toString()
            val poiType = spinnerPoiType.selectedItem.toString()

            if (radiusText.isNotEmpty()) {
                val radius = radiusText.toInt()
                getNearbyPointsOfInterest(userLocation, radius, poiType)
                dialog.dismiss()
            } else {
                // Manejar el caso en el que el campo de radio está vacío
                // Puedes mostrar un mensaje al usuario o realizar alguna acción
            }
        }

        dialog.show()
    }


    private fun getLastLocationAndShowOnMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Obtener ubicación actual y mostrar en el mapa
                    userLocation = GeoPoint(it.latitude, it.longitude)
                    mapView.controller.setCenter(userLocation)

                    // Agregar marcador de ubicación actual
                    userLocationMarker = Marker(mapView)
                    userLocationMarker.position = userLocation
                    userLocationMarker.title = "Ubicación Actual"
                    userLocationMarker.icon = ContextCompat.getDrawable(this,R.drawable.ic_user_location)
                    mapView.overlays.add(userLocationMarker)

                    //getNearbyPointsOfInterest(userLocation!!)
                }
            }
    }

    private fun getNearbyPointsOfInterest(userLocation: GeoPoint, radius: Int, poiType: String) {
        //val radius = 3000 // Ejemplo de radio en metros
        val overpassApiUrl = when (poiType){
            "Hoteles"-> "https://overpass-api.de/api/interpreter?data=[out:json];" +
                        "(" +
                        "node[tourism=hotel](around:${radius},${userLocation.latitude},${userLocation.longitude});" +
                        ");"+
                        "out center;"
            "Hospitales" -> "https://overpass-api.de/api/interpreter?data=[out:json];" +
                            "(" +
                            "node[amenity=hospital](around:${radius},${userLocation.latitude},${userLocation.longitude});" +
                            ");"+
                            "out center;"
            "Museos" -> "https://overpass-api.de/api/interpreter?data=[out:json];" +
                    "(" +
                    "node[tourism=museum](around:${radius},${userLocation.latitude},${userLocation.longitude});" +
                    ");"+
                    "out center;"
            "Cafeterías" -> "https://overpass-api.de/api/interpreter?data=[out:json];" +
                    "(" +
                    "node[amenity=cafe](around:${radius},${userLocation.latitude},${userLocation.longitude});" +
                    ");"+
                    "out center;"
            "Lugares turísticos" -> "https://overpass-api.de/api/interpreter?data=[out:json];" +
                    "(" +
                    "node[tourism=attraction](around:${radius},${userLocation.latitude},${userLocation.longitude});" +
                    ");"+
                    "out center;"
            else -> {
                // Tratamiento para el caso en que el tipo de POI no esté definido
                return
            }
        }
            /*"https://overpass-api.de/api/interpreter?data=[out:json];" +
                    "(" +
                    "node[tourism=hotel](around:${radius},${userLocation.latitude},${userLocation.longitude});" +
                    "node[amenity=cafe](around:${radius},${userLocation.latitude},${userLocation.longitude});" +
                    ");"+
                    "out center;"*/

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, overpassApiUrl, null,
            { response ->
                // Manejar la respuesta JSON
                parseAndShowPointsOfInterest(response.toString())
            },
            { error ->
                // Manejar errores de la solicitud
                error.printStackTrace()
            })
        requestQueue=Volley.newRequestQueue(this)
        // Agregar la solicitud a la cola de Volley
        requestQueue.add(jsonObjectRequest)
    }

    private fun parseAndShowPointsOfInterest(responseData: String) {
        val json = JSONObject(responseData)

        // Limpiar marcadores existentes
        mapView.overlays.clear()

        // Crear capas para hoteles y cafés
        val hotelOverlay = FolderOverlay()
        val cafeOverlay = FolderOverlay()

        // Agregar marcadores para los puntos de interés encontrados
        val elements = json.getJSONArray("elements")
        for (i in 0 until elements.length()) {
            val element = elements.getJSONObject(i)
            val lat = element.getDouble("lat")
            val lon = element.getDouble("lon")
            val tags = element.optJSONObject("tags")
            val name = tags?.optString("name", "")
            val amenity = tags?.optString("amenity", "")
            val tourism = tags?.optString("tourism","")

            val poiLocation = GeoPoint(lat, lon)
            val poiMarker = Marker(mapView)
            poiMarker.position = poiLocation
            poiMarker.title = name
            //mapView.overlays.add(poiMarker)
            // Diferenciar por amenity y asignar colores
            when (amenity) {
                "cafe" -> {
                    poiMarker.icon = ContextCompat.getDrawable(this, R.drawable.ic_cafe_location)
                    cafeOverlay.add(poiMarker)
                }
                // Puedes agregar más casos según sea necesario
                else -> {
                    // Otro tipo de punto de interés
                    mapView.overlays.add(poiMarker)
                }
            }

            when (tourism){
                "hotel" -> {
                    poiMarker.icon = ContextCompat.getDrawable(this, R.drawable.ic_location_hotel)
                    hotelOverlay.add(poiMarker)
                }
            }
        }

        // Agregar las capas al mapa
        mapView.overlays.add(userLocationMarker)
        mapView.overlays.add(hotelOverlay)
        mapView.overlays.add(cafeOverlay)
        // Actualizar el mapa
        mapView.invalidate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, obtener ubicación y mostrar en el mapa
                getLastLocationAndShowOnMap()
            } else {
                // Permiso denegado, manejar la situación
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }
}


