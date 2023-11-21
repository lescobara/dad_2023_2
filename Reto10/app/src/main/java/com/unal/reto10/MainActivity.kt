package com.unal.reto10

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var apiRequestManager: ApiRequestManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageViewCenter: ImageView
    private lateinit var sensorAdapter: SensorAdapter
    private lateinit var sensorViewModel: SensorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar la barra de herramientas solo si el tema no incluye una "action bar"
        if (supportActionBar == null) {
            setSupportActionBar(findViewById(R.id.toolbar))
        }

        apiRequestManager = ApiRequestManager(this)
        imageViewCenter = findViewById(R.id.imageViewCenter)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        sensorAdapter = SensorAdapter(emptyList())
        recyclerView.adapter = sensorAdapter

        sensorViewModel = ViewModelProvider(this).get(SensorViewModel::class.java)

        // Verificar si hay datos almacenados en el ViewModel después de recrear la actividad
        sensorViewModel.sensorList?.let { sensorList ->
            if (sensorList.isNotEmpty()) {
                imageViewCenter.visibility = View.GONE
                sensorAdapter = SensorAdapter(sensorList)
                recyclerView.adapter = sensorAdapter
                recyclerView.visibility = View.VISIBLE
            }
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
                // Lógica para la acción "Buscar" sensores por departamento
                showSearchDialog()
                return true
            }

            R.id.action_search2 -> {
                // Lógica para la acción "Buscar" sensores a nivel nacional
                showNationalSearchDialog()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showSearchDialog() {
        apiRequestManager.fetchDepartments { departments ->
            if (departments.isNotEmpty()) {
                val descriptions = emptyList<String>() // Esto lo dejaré vacío para mantener el enfoque en los departamentos
                showSelectionDialog(departments, descriptions)
            } else {
                Toast.makeText(this, "No se encontraron departamentos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSelectionDialog(departments: List<String>, descriptions: List<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona departamento")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_search, null)
        val departmentSpinner: Spinner = dialogLayout.findViewById(R.id.departmentSpinner)

        // Configurar adaptadores para los spinners
        val departmentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        departmentSpinner.adapter = departmentAdapter

        builder.setView(dialogLayout)
        builder.setPositiveButton("Buscar") { _, _ ->
            val selectedDepartment = departmentSpinner.selectedItem.toString()

            // Aquí puedes realizar la acción de búsqueda con los valores seleccionados
            // Puedes llamar a una función para procesar la búsqueda con los valores seleccionados
            performSearch(selectedDepartment)
        }
        builder.setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun performSearch(department: String) {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        apiRequestManager.fetchAllRecordsForDepartment(department) { sensorStrings ->
            val sensorList = mutableListOf<Sensor>()
            sensorStrings.forEach { sensorString ->
                val jsonObject = JSONObject(sensorString)
                val codigoEstacion = jsonObject.getString("codigoestacion")
                val municipio = jsonObject.getString("municipio")
                val latitud = jsonObject.getString("latitud")
                val longitud = jsonObject.getString("longitud")
                val descripcionSensor = jsonObject.getString("descripcionsensor")
                val nombre = jsonObject.getString("nombreestacion")
                val entidad = jsonObject.getString("entidad")

                val sensor = Sensor(codigoEstacion, municipio, latitud, longitud, descripcionSensor,nombre,entidad)
                sensorList.add(sensor)
            }

            runOnUiThread {
                if (sensorList.isNotEmpty()) {
                    imageViewCenter.visibility = View.GONE
                    sensorAdapter = SensorAdapter(sensorList)
                    recyclerView.adapter = sensorAdapter
                    recyclerView.visibility = View.VISIBLE
                    // Guardar la lista en el ViewModel
                    sensorViewModel.sensorList = sensorList
                } else {
                    Toast.makeText(this, "No se encontraron sensores para $department", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showNationalSearchDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Buscar sensores a nivel nacional")

        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_national_search, null)
        val sensorSpinner: Spinner = dialogLayout.findViewById(R.id.sensorSpinner)
        val limitEditText: EditText = dialogLayout.findViewById(R.id.limitEditText)

        // Lógica para obtener la lista de tipos de sensores a nivel nacional
        apiRequestManager.fetchSensorsTypes { sensorList ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sensorList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sensorSpinner.adapter = adapter
        }

        builder.setView(dialogLayout)
        builder.setPositiveButton("Buscar") { _, _ ->
            val selectedSensor = sensorSpinner.selectedItem.toString()
            val limit = limitEditText.text.toString().toIntOrNull() ?: 0

            // Aquí se realiza la búsqueda con el sensor seleccionado y el límite ingresado
            performNationalSearch(selectedSensor, limit)
        }
        builder.setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun performNationalSearch(selectedSensor:String,limit: Int){

        apiRequestManager.fetchSensorsByType(selectedSensor,limit){sensorStrings ->
            val sensorList = mutableListOf<Sensor>()
            sensorStrings.forEach { sensorString ->
                val jsonObject = JSONObject(sensorString)
                val codigoEstacion = jsonObject.getString("codigoestacion")
                val municipio = jsonObject.getString("municipio")
                val latitud = jsonObject.getString("latitud")
                val longitud = jsonObject.getString("longitud")
                val descripcionSensor = jsonObject.getString("descripcionsensor")
                val nombre = jsonObject.getString("nombreestacion")
                val entidad = jsonObject.getString("entidad")

                val sensor = Sensor(codigoEstacion, municipio, latitud, longitud, descripcionSensor,nombre,entidad)
                sensorList.add(sensor)
            }
            runOnUiThread {
                if (sensorList.isNotEmpty()) {
                    val intent = Intent(this@MainActivity, MapNationalActivity::class.java)
                    intent.putParcelableArrayListExtra("SENSOR_LIST", ArrayList(sensorList))
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se encontraron sensores para $selectedSensor", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}