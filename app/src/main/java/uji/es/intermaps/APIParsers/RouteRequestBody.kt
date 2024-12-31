package uji.es.intermaps.APIParsers

data class RouteRequestBody(
    val coordinates: List<List<Double>>,
    val preference: String,
    val units: String = "m",
    val geometry: Boolean = true,
    val instructions: Boolean = true,
    val language: String = "es",
    val extra_info: List<String>? = null
)