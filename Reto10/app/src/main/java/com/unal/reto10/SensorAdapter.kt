package com.unal.reto10

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SensorAdapter(private val sensorList: List<Sensor>) :
    RecyclerView.Adapter<SensorAdapter.SensorViewHolder>() {

    class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val codigoEstacion: TextView = itemView.findViewById(R.id.textCodigoEstacion)
        val municipio: TextView = itemView.findViewById(R.id.textMunicipio)
        val latitud: TextView = itemView.findViewById(R.id.textLatitud)
        val longitud: TextView = itemView.findViewById(R.id.textLongitud)
        val descripcionSensor: TextView = itemView.findViewById(R.id.textDescripcionSensor)
        val nombre : TextView = itemView.findViewById(R.id.textNombre)
        val entidad : TextView = itemView.findViewById(R.id.textEntidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sensor_item, parent, false)
        return SensorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val currentItem = sensorList[position]
        holder.codigoEstacion.text = currentItem.codigoEstacion
        holder.municipio.text = currentItem.municipio
        holder.latitud.text = currentItem.latitud
        holder.longitud.text = currentItem.longitud
        holder.descripcionSensor.text = currentItem.descripcionSensor
        holder.nombre.text = currentItem.nombre
        holder.entidad.text = currentItem.entidad

        // Configurar OnClickListener para el ícono del mapa
        holder.itemView.findViewById<ImageView>(R.id.imageViewMapIcon).setOnClickListener {
            // Obtener latitud y longitud del sensor actual
            val latitud = currentItem.latitud
            val longitud = currentItem.longitud
            val nombre = currentItem.nombre
            val entidad = currentItem.entidad

            // Crear un Intent para abrir la actividad del mapa y pasar la latitud y longitud
            val intent = Intent(holder.itemView.context, MapActivity::class.java)
            intent.putExtra("LATITUDE", latitud)
            intent.putExtra("LONGITUDE", longitud)
            intent.putExtra("NOMBRE",nombre)
            intent.putExtra("ENTIDAD",entidad)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount() = sensorList.size
}

