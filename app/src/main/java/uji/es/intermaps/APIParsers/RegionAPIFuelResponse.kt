package uji.es.intermaps.APIParsers

data class RegionAPIFuelResponse (
    val features: List<PossibleRegion>
)

data class PossibleRegionFuel(
    val properties: PlaceData,
    val region: String

)
