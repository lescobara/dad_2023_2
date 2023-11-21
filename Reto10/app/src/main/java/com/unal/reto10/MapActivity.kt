package com.unal.reto10

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.preference.PreferenceManager
import android.widget.Button

class MapActivity:AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Cierra esta actividad y vuelve a la actividad anterior
        }

        val latitud = intent.getStringExtra("LATITUDE")?.toDouble() ?:0.0
        val longitud = intent.getStringExtra("LONGITUDE")?.toDouble() ?:0.0
        val nombre = intent.getStringExtra("NOMBRE")
        val entidad = intent.getStringExtra("ENTIDAD")
        // Código para mostrar el mapa usando osmdroid con latitud y longitud
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))


        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        val startPoint = GeoPoint(latitud, longitud)
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(15.0)

        // Agregar marcador de ubicación actual
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.title = "Nombre:$nombre"
        val snippet = "Latitud: $latitud<br/>Longitud: $longitud<br/>Entidad: $entidad"
        marker.snippet = snippet

        mapView.overlays.add(marker)
    }

}