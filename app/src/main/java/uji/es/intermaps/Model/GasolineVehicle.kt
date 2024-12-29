package uji.es.intermaps.Model

import android.os.Parcel
import android.os.Parcelable

class GasolineVehicle (plate: String, type: String, consumption: Double, fav: Boolean) : Vehicle(plate, type, consumption, fav),
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readValue(Boolean::class.java.classLoader) as Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        writeToParcelBase(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GasolineVehicle> {
        override fun createFromParcel(parcel: Parcel): GasolineVehicle {
            return GasolineVehicle(parcel)
        }

        override fun newArray(size: Int): Array<GasolineVehicle?> {
            return arrayOfNulls(size)
        }
    }
}