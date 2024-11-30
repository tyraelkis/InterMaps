package uji.es.intermaps.APIParsers

import uji.es.intermaps.Model.Coordinate

data class ToponymToCoordORSAPIResponse(
    val features: List<PossibleCoord>
)

data class PossibleCoord(
    val properties: PlaceData,
    val coordinates: Coordinate
)