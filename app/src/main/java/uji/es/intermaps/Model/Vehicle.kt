package uji.es.intermaps.Model

import android.os.Parcel
import android.os.Parcelable

abstract class Vehicle(
    val plate: String = "",
    val type: String = "",
    val consumption: Double= 0.0,
    val fav: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readValue(Boolean::class.java.classLoader) as Boolean
    )

    fun writeToParcelBase(parcel: Parcel, flags: Int) {
        parcel.writeValue(plate)
        parcel.writeString(type)
        parcel.writeDouble(consumption)
        parcel.writeValue(fav)
    }
}