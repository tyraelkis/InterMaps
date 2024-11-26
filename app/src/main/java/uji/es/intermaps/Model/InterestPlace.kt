package uji.es.intermaps.Model

import com.google.firebase.firestore.GeoPoint

data class InterestPlace(val coordinate: GeoPoint = GeoPoint(0.0, 0.0),
                         val toponym: String = "",
                         var alias: String = "",
                         var fav: Boolean = false) {}