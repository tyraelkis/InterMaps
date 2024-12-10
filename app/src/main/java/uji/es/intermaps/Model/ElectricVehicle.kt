package uji.es.intermaps.Model

import android.os.Parcel
import android.os.Parcelable

class ElectricVehicle (plate: String, type: String, consumption: Double, fav: Boolean) : Vehicle(plate, type, consumption, fav),
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

    companion object CREATOR : Parcelable.Creator<ElectricVehicle> {
        override fun createFromParcel(parcel: Parcel): ElectricVehicle {
            return ElectricVehicle(parcel)
        }

        override fun newArray(size: Int): Array<ElectricVehicle?> {
            return arrayOfNulls(size)
        }
    }
}