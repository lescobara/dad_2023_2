package com.unal.reto10

import android.os.Parcel
import android.os.Parcelable

data class Sensor(
    val codigoEstacion: String,
    val municipio: String,
    val latitud: String,
    val longitud: String,
    val descripcionSensor: String,
    val nombre : String,
    val entidad : String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(codigoEstacion)
        parcel.writeString(municipio)
        parcel.writeString(latitud)
        parcel.writeString(longitud)
        parcel.writeString(descripcionSensor)
        parcel.writeString(nombre)
        parcel.writeString(entidad)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sensor> {
        override fun createFromParcel(parcel: Parcel): Sensor {
            return Sensor(parcel)
        }

        override fun newArray(size: Int): Array<Sensor?> {
            return arrayOfNulls(size)
        }
    }
}
