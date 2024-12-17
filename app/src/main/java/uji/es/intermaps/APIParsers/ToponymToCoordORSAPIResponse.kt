package uji.es.intermaps.APIParsers


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
