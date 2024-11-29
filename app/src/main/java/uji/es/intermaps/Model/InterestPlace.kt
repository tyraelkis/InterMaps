package uji.es.intermaps.Model

import com.google.firebase.firestore.GeoPoint


data class InterestPlace(
    val coordinate: GeoPoint = GeoPoint(0.0, 0.0),
    val toponym: String = "",
    var alias: String = "",
    var fav: Boolean = false
) :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(GeoPoint::class.java.classLoader) as GeoPoint,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readValue(Boolean::class.java.classLoader) as Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(coordinate)
        parcel.writeString(toponym)
        parcel.writeString(alias)
        parcel.writeValue(fav)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<InterestPlace> {
        override fun createFromParcel(parcel: Parcel): InterestPlace {
            return InterestPlace(parcel)
        }

        override fun newArray(size: Int): Array<InterestPlace?> {
            return arrayOfNulls(size)
        }
    }

}
