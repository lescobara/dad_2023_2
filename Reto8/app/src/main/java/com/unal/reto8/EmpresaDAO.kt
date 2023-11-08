package com.unal.reto8

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EmpresaDAO(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "EmpresasDB.db"
        private const val TABLE_EMPRESAS = "empresas"

        // Definición de las columnas
        private const val KEY_ID = "id"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_URL = "url"
        private const val KEY_TELEFONO = "telefono"
        private const val KEY_EMAIL = "email"
        private const val KEY_PRODUCTOS_SERVICIOS = "productos_servicios"
        private const val KEY_CLASIFICACION = "clasificacion"
    }

    override fun onCreate(db: SQLiteDatabase) {

        val CREATE_EMPRESAS_TABLE = (
                "CREATE TABLE $TABLE_EMPRESAS(" +
                        "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$KEY_NOMBRE TEXT," +
                        "$KEY_URL TEXT," +
                        "$KEY_TELEFONO TEXT," +
                        "$KEY_EMAIL TEXT," +
                        "$KEY_PRODUCTOS_SERVICIOS TEXT," +
                        "$KEY_CLASIFICACION TEXT)"
                )
        db.execSQL(CREATE_EMPRESAS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EMPRESAS")
        onCreate(db)
    }

    @SuppressLint("Range")
    fun obtenerEmpresas(): List<Empresa> {
        val empresas = ArrayList<Empresa>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_EMPRESAS"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val empresa = Empresa(
                   //cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)),
                    cursor.getString(cursor.getColumnIndex(KEY_URL)),
                    cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)),
                    cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(KEY_PRODUCTOS_SERVICIOS)),
                    cursor.getString(cursor.getColumnIndex(KEY_CLASIFICACION))
                )
                empresa.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)))
                empresas.add(empresa)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return empresas
    }

    // Agregar funciones para insertar, actualizar y eliminar empresas si es necesario
    fun insertarEmpresa(empresa: Empresa) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(KEY_NOMBRE, empresa.getNombre())
        values.put(KEY_URL, empresa.getUrl())
        values.put(KEY_TELEFONO, empresa.getTelefono())
        values.put(KEY_EMAIL, empresa.getEmail())
        values.put(KEY_PRODUCTOS_SERVICIOS, empresa.getProductosServicios())
        values.put(KEY_CLASIFICACION, empresa.getClasificacion())
        db.insert(TABLE_EMPRESAS, null, values)
        db.close()
    }

    fun borrarTodasEmpresas(){
        val db = readableDatabase
        db.execSQL("DELETE FROM $TABLE_EMPRESAS")
    }

    @SuppressLint("Range")
    fun obtenerEmpresaPorId(idEmpresa: Int): Empresa? {
        val db = readableDatabase
        var empresa: Empresa? = null

        val query = "SELECT * FROM $TABLE_EMPRESAS WHERE $KEY_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(idEmpresa.toString()))

        if (cursor.moveToFirst()) {
            empresa = Empresa(
                cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)),
                cursor.getString(cursor.getColumnIndex(KEY_URL)),
                cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)),
                cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                cursor.getString(cursor.getColumnIndex(KEY_PRODUCTOS_SERVICIOS)),
                cursor.getString(cursor.getColumnIndex(KEY_CLASIFICACION))
            )
            empresa.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)))
        }

        cursor.close()
        db.close()

        return empresa
    }

    fun actualizarEmpresa(empresa: Empresa) {
        val db = writableDatabase

        val values = ContentValues()
        values.put(KEY_NOMBRE, empresa.getNombre())
        values.put(KEY_URL, empresa.getUrl())
        values.put(KEY_TELEFONO, empresa.getTelefono())
        values.put(KEY_EMAIL, empresa.getEmail())
        values.put(KEY_PRODUCTOS_SERVICIOS, empresa.getProductosServicios())
        values.put(KEY_CLASIFICACION, empresa.getClasificacion())

        // Actualiza la empresa en la base de datos utilizando el ID como condición
        db.update(
            TABLE_EMPRESAS,
            values,
            "$KEY_ID = ?",
            arrayOf(empresa.getId().toString())
        )
        db.close()
    }

    fun eliminarEmpresa(idEmpresa: Int) {
        val db = writableDatabase

        // Realiza la eliminación de la empresa en la base de datos utilizando el ID como condición
        db.delete(
            TABLE_EMPRESAS,
            "$KEY_ID = ?",
            arrayOf(idEmpresa.toString())
        )
        db.close()
    }

    @SuppressLint("Range")
    fun buscarEmpresasPorNombre(nombre: String): List<Empresa> {
        val empresas = ArrayList<Empresa>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_EMPRESAS WHERE $KEY_NOMBRE LIKE ?"
        val cursor = db.rawQuery(query, arrayOf("%$nombre%"))

        if (cursor.moveToFirst()) {
            do {
                val empresa = Empresa(
                    cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)),
                    cursor.getString(cursor.getColumnIndex(KEY_URL)),
                    cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)),
                    cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(KEY_PRODUCTOS_SERVICIOS)),
                    cursor.getString(cursor.getColumnIndex(KEY_CLASIFICACION))
                )
                empresa.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)))
                empresas.add(empresa)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return empresas
    }

    @SuppressLint("Range")
    fun buscarEmpresasPorClasificacion(clasificacion: String): List<Empresa> {
        val empresas = ArrayList<Empresa>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_EMPRESAS WHERE $KEY_CLASIFICACION = ?"
        val cursor = db.rawQuery(query, arrayOf(clasificacion))

        if (cursor.moveToFirst()) {
            do {
                val empresa = Empresa(
                    cursor.getString(cursor.getColumnIndex(KEY_NOMBRE)),
                    cursor.getString(cursor.getColumnIndex(KEY_URL)),
                    cursor.getString(cursor.getColumnIndex(KEY_TELEFONO)),
                    cursor.getString(cursor.getColumnIndex(KEY_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(KEY_PRODUCTOS_SERVICIOS)),
                    cursor.getString(cursor.getColumnIndex(KEY_CLASIFICACION))
                )
                empresa.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)))
                empresas.add(empresa)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return empresas
    }


}
