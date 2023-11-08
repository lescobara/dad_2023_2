package com.unal.reto8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Context

class EmpresaAdapter(private val context: Context,private val empresas: List<Empresa>) : RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder>() {
    inner class EmpresaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textId : TextView= itemView.findViewById(R.id.textId)
        val nombre: TextView = itemView.findViewById(R.id.textNombre)
        val url: TextView = itemView.findViewById(R.id.textURL)
        val telefono: TextView = itemView.findViewById(R.id.textTelefono)
        val email: TextView = itemView.findViewById(R.id.textEmail)
        val productos: TextView = itemView.findViewById(R.id.textProductos)
        val clasificacion: TextView = itemView.findViewById(R.id.textClasificacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_empresa, parent, false)
        return EmpresaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        val empresa = empresas[position]

        // Obtén referencias a las imágenes de edición y eliminación
        val imageEditar = holder.itemView.findViewById<ImageView>(R.id.imageEditar_1)
        val imageEliminar = holder.itemView.findViewById<ImageView>(R.id.imageEliminar_1)

        holder.textId.text = empresa.getId().toString()
        holder.nombre.text = empresa.getNombre()
        holder.url.text = empresa.getUrl()
        holder.telefono.text = empresa.getTelefono()
        holder.email.text = empresa.getEmail()
        holder.productos.text = empresa.getProductosServicios()
        holder.clasificacion.text = empresa.getClasificacion()
        // Configura los campos para las otras columnas

        imageEditar.setOnClickListener {
            // Aquí puedes usar el ID de la empresa para abrir el diálogo de edición
            (context as MainActivity).mostrarDialogoEdicion(empresa.getId())
        }

        imageEliminar.setOnClickListener {
            (context as MainActivity).mostrarDialogoEliminar(empresa.getId())
        }

    }

    override fun getItemCount() = empresas.size
}
