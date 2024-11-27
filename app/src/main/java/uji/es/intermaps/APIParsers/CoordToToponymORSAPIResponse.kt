package uji.es.intermaps.APIParsers

data class CoordToToponymORSAPIResponse(
    val features: List<PossiblePlace>
)

data class PossiblePlace(
    val properties: PlaceData
)

data class PlaceData(
    val name: String,           //Nombre del lugar
    val country: String,        //País
    val macroregion: String,    //Comunidad autónoma
    val region: String,         //Provincia
    val locality: String,       //Ciudad o Pueblo
    val label: String           //Dirección completa
)