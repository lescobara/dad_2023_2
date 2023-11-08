package com.unal.reto8

class Empresa(
    //private var id: Long,
    private var nombre: String,
    private var url: String,
    private var telefono: String,
    private var email: String,
    private var productosServicios: String,
    private var clasificacion: String
)
{
    private var id: Int = 0
    // Métodos para acceder a las propiedades

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getNombre(): String {
        return nombre
    }

    fun setNombre(nombre: String) {
        this.nombre = nombre
    }

    fun getUrl(): String {
        return url
    }

    fun setUrl(url: String) {
        this.url = url
    }

    fun getTelefono(): String {
        return telefono
    }

    fun setTelefono(telefono: String) {
        this.telefono = telefono
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getProductosServicios(): String {
        return productosServicios
    }

    fun setProductosServicios(productosServicios: String) {
        this.productosServicios = productosServicios
    }

    fun getClasificacion(): String {
        return clasificacion
    }

    fun setClasificacion(clasificacion: String) {
        this.clasificacion = clasificacion
    }
}


