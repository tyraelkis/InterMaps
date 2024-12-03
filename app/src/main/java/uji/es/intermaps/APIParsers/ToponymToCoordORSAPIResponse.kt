package uji.es.intermaps.APIParsers

import android.graphics.Point
import uji.es.intermaps.Model.Coordinate

data class ToponymToCoordORSAPIResponse(
    val features: List<PossibleCoord>
)

data class PossibleCoord(
    val properties: PlaceData,
    val geometry: Geometry

)
data class Geometry(
    val coordinates: List<Double>,
)

