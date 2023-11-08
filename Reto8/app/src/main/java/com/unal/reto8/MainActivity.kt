package com.unal.reto8

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        crearMuestra()
        consultar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_app, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_nuevo -> {
                // Lógica para la acción "Nuevo"
                mostrarDialogoNuevoEmpresa()
                return true
            }
            R.id.action_buscar_nombre -> {
                // Lógica para la búsqueda por nombre
                mostrarDialogoBusquedaPorNombre()
                return true
            }
            R.id.action_buscar_clasificacion -> {
                // Lógica para la búsqueda por clasificación
                mostrarDialogoBusquedaPorClasificacion()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // La pantalla está en orientación landscape
            // Realiza acciones específicas para landscape
            setContentView(R.layout.activity_main_lyt)
            setSupportActionBar(findViewById(R.id.toolbar))
            consultar()
        } else {
            // La pantalla está en orientación portrait
            // Realiza acciones específicas para portrait
            setContentView(R.layout.activity_main)
            setSupportActionBar(findViewById(R.id.toolbar))
            consultar()
        }
    }

    private fun consultar(){

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbDAO = EmpresaDAO(this)
        // Obtén los datos de las empresas desde tu base de datos SQLite y guárdalos en una lista.
        val empresas = dbDAO.obtenerEmpresas()

        val adapter = EmpresaAdapter(this,empresas)
        recyclerView.adapter = adapter
    }

    private fun crearMuestra(){
        val dbHelper = EmpresaDAO(this)
        dbHelper.borrarTodasEmpresas()
        val empresaA=Empresa("Empresa A", "www.empresa-a.com", "123-456-7890", "contacto@empresa-a.com", "Servicio A", "Consultoría")
        val empresaB=Empresa("Empresa B", "www.empresa-b.com", "987-654-3210", "contacto@empresa-b.com", "Servicio B", "Desarrollo a medida")
        val empresaC=Empresa("Empresa C", "www.empresa-c.com", "111-222-3333", "contacto@empresa-c.com", "Servicio C", "Fábrica de software")

        dbHelper.insertarEmpresa(empresaA)
        dbHelper.insertarEmpresa(empresaB)
        dbHelper.insertarEmpresa(empresaC)
    }

    private fun mostrarDialogoNuevoEmpresa() {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_nueva_empresa, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setTitle("Nuevo Registro")

        val spinnerTipoEmpresa = dialogView.findViewById<Spinner>(R.id.editTipoEmpresa)
        val opcionesTipoEmpresa = arrayOf("Consultoría", "Desarrollo a medida", "Fábrica de software") // Define las opciones que deseas mostrar

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoEmpresa)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoEmpresa.adapter = adapter

        spinnerTipoEmpresa.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Aquí puedes capturar la opción seleccionada por el usuario
                val tipoEmpresaSeleccionado = opcionesTipoEmpresa[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Puedes manejar el caso en el que no se seleccione nada
            }
        }

        builder.setPositiveButton("Guardar") { dialog, which ->
            // Aquí puedes obtener los datos ingresados por el usuario desde el dialogView y guardarlos en la base de datos
            val nombreEmpresa = dialogView.findViewById<EditText>(R.id.editNombreEmpresa).text.toString()
            val urlEmpresa = dialogView.findViewById<EditText>(R.id.editUrlEmpresa).text.toString()
            val telefono = dialogView.findViewById<EditText>(R.id.editTelefonoContacto).text.toString()
            val email = dialogView.findViewById<EditText>(R.id.editEmailContacto).text.toString()
            val productos = dialogView.findViewById<EditText>(R.id.editProductosServicios).text.toString()
            val tipo = opcionesTipoEmpresa[spinnerTipoEmpresa.selectedItemPosition]

            // Obtén los otros datos ingresados de manera similar

            // Luego, guarda los datos en la base de datos
            val nuevaEmpresa = Empresa(nombreEmpresa, urlEmpresa, telefono,email,productos,tipo)
            val dbHelper = EmpresaDAO(this)
            dbHelper.insertarEmpresa(nuevaEmpresa)

            // Actualiza la vista del RecyclerView o tabla con los nuevos datos si es necesario
            consultar()
        }

        builder.setNegativeButton("Cancelar") { dialog, which -> }

        val dialog = builder.create()
        dialog.show()
    }

    fun mostrarDialogoEdicion(idEmpresa: Int) {
        // Aquí implementa el diálogo de edición usando el ID de la empresa
        val empresaDAO = EmpresaDAO(this)
        val empresa = empresaDAO.obtenerEmpresaPorId(idEmpresa)

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogo_nueva_empresa, null)

        // Configura el contenido del diálogo con los datos de la empresa

        if (empresa != null) {
            dialogView.findViewById<EditText>(R.id.editNombreEmpresa).setText(empresa.getNombre())
            dialogView.findViewById<EditText>(R.id.editUrlEmpresa).setText(empresa.getUrl())
            dialogView.findViewById<EditText>(R.id.editTelefonoContacto).setText(empresa.getTelefono())
            dialogView.findViewById<EditText>(R.id.editEmailContacto).setText(empresa.getEmail())
            dialogView.findViewById<EditText>(R.id.editProductosServicios).setText(empresa.getProductosServicios())
            val spinnerTipoEmpresa = dialogView.findViewById<Spinner>(R.id.editTipoEmpresa)
            val opcionesTipoEmpresa = arrayOf("Consultoría", "Desarrollo a medida", "Fábrica de software") // Define las opciones que deseas mostrar

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoEmpresa)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTipoEmpresa.adapter = adapter

            val tipoEmpresa = empresa.getClasificacion() // Obtiene el valor actual del tipo de empresa

            val posicion = opcionesTipoEmpresa.indexOf(tipoEmpresa)
            if (posicion != -1) {
                spinnerTipoEmpresa.setSelection(posicion)
            }
        }

        // Configura otros campos con los datos de la empresa

        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Editar Empresa")

        dialogBuilder.setPositiveButton("Guardar") { dialog, which ->
            // Aquí obtén los datos editados desde el diálogo y actualiza la empresa en la base de datos
            val nombreEmpresa = dialogView.findViewById<EditText>(R.id.editNombreEmpresa).text.toString()
            val urlEmpresa = dialogView.findViewById<EditText>(R.id.editUrlEmpresa).text.toString()
            val telefono = dialogView.findViewById<EditText>(R.id.editTelefonoContacto).text.toString()
            val email = dialogView.findViewById<EditText>(R.id.editEmailContacto).text.toString()
            val productos = dialogView.findViewById<EditText>(R.id.editProductosServicios).text.toString()
            val spinnerTipoEmpresa = dialogView.findViewById<Spinner>(R.id.editTipoEmpresa)
            val tipo = spinnerTipoEmpresa.selectedItem.toString()
            // Obtén otros campos editados y actualiza la empresa en la base de datos
            if (empresa != null) {
                empresa.setNombre(nombreEmpresa)
                empresa.setUrl(urlEmpresa)
                empresa.setTelefono(telefono)
                empresa.setEmail(email)
                empresa.setProductosServicios(productos)
                empresa.setClasificacion(tipo)
            }

            // Actualiza otros campos

            if (empresa != null) {
                empresaDAO.actualizarEmpresa(empresa)
            }
            // Actualiza la vista del RecyclerView si es necesario
            consultar()
        }

        dialogBuilder.setNegativeButton("Cancelar") { dialog, which -> }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    fun mostrarDialogoEliminar(idEmpresa: Int) {
        val empresaDAO = EmpresaDAO(this)
        val empresa = empresaDAO.obtenerEmpresaPorId(idEmpresa)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Eliminar Empresa")
        if (empresa != null) {
            dialogBuilder.setMessage("¿Seguro que desea eliminar la empresa ${empresa.getNombre()}?")
        }

        dialogBuilder.setPositiveButton("Eliminar") { dialog, which ->
            // Elimina la empresa de la base de datos
            empresaDAO.eliminarEmpresa(idEmpresa)

            // Actualiza la vista del RecyclerView o tabla con los nuevos datos si es necesario
            consultar()
        }

        dialogBuilder.setNegativeButton("Cancelar") { dialog, which -> }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun mostrarDialogoBusquedaPorNombre() {
        val empresaDAO = EmpresaDAO(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.busqueda_nombre, null)

        builder.setView(dialogView)
        builder.setTitle("Búsqueda por Nombre")

        builder.setPositiveButton("Buscar") { dialog, which ->
            // Aquí puedes obtener el texto ingresado por el usuario y realizar la búsqueda
            val textoBusqueda = dialogView.findViewById<EditText>(R.id.editTextoBusqueda).text.toString()

            // Lleva a cabo la búsqueda según el texto ingresado
            val empresas=empresaDAO.buscarEmpresasPorNombre(textoBusqueda)
            val adapter = EmpresaAdapter(this,empresas)
            recyclerView.adapter = adapter
        }

        builder.setNegativeButton("Cancelar") { dialog, which -> }

        val dialog = builder.create()
        dialog.show()
    }

    private fun mostrarDialogoBusquedaPorClasificacion() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val empresaDAO = EmpresaDAO(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dialogView = inflater.inflate(R.layout.busqueda_tipo, null)
        val spinnerTipoEmpresa = dialogView.findViewById<Spinner>(R.id.spinnerClasificacion)
        val opcionesTipoEmpresa = arrayOf("Consultoría", "Desarrollo a medida", "Fábrica de software") // Define las opciones que deseas mostrar

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesTipoEmpresa)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoEmpresa.adapter = adapter

        spinnerTipoEmpresa.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Aquí puedes capturar la opción seleccionada por el usuario
                val tipoEmpresaSeleccionado = opcionesTipoEmpresa[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Puedes manejar el caso en el que no se seleccione nada
            }
        }

        builder.setView(dialogView)
        builder.setTitle("Búsqueda por Clasificación")

        builder.setPositiveButton("Buscar") { dialog, which ->
            // Aquí puedes obtener la clasificación seleccionada por el usuario y realizar la búsqueda
            val seleccionClasificacion = spinnerTipoEmpresa.selectedItem.toString()

            // Lleva a cabo la búsqueda según la clasificación seleccionada
            val empresas=empresaDAO.buscarEmpresasPorClasificacion(seleccionClasificacion)
            val adapter = EmpresaAdapter(this,empresas)
            recyclerView.adapter = adapter
        }

        builder.setNegativeButton("Cancelar") { dialog, which -> }

        val dialog = builder.create()
        dialog.show()
    }


}