package com.unal.reto10

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapNationalActivity: AppCompatActivity() {
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Cierra esta actividad y vuelve a la actividad anterior
        }

        val sensors: List<Sensor> = intent.getParcelableArrayListExtra("SENSOR_LIST") ?: emptyList()

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        val colombiaCenter = GeoPoint(4.5709, -74.2973)
        mapView.controller.setCenter(colombiaCenter)
        mapView.controller.setZoom(7.0)

        val overlays = mapView.overlays
        for (sensor in sensors) {
            val startPoint = GeoPoint(sensor.latitud.toDouble(), sensor.longitud.toDouble())
            val marker = Marker(mapView)
            marker.position = startPoint
            marker.title = sensor.nombre
            val snippet = "Latitud: ${sensor.latitud}<br/>Longitud: ${sensor.longitud}<br/>Entidad: ${sensor.entidad}<br/>Municipio: ${sensor.municipio}<br/>Descripción: ${sensor.descripcionSensor}"
            marker.snippet = snippet

            overlays.add(marker)
        }
    }
}