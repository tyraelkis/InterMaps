package uji.es.intermaps.APIParsers


data class RegionORSAPIResponse(
    val features: List<PossibleRegion>
)

data class PossibleRegion(
    val properties: PlaceData,
    val region: String

)

