package uji.es.intermaps.APIParsers

data class RouteRequestBody(
    val coordinates: List<List<Double>>, // Coordenadas de la ruta (origen, destino)
    val alternative_routes: AlternativeRoutes? = null, // Opcional: para rutas alternativas
    val preference: String = "shortest", // Preferencia por la ruta (ej. shortest, recommended, etc.)
    val units: String = "m", // Unidades
    val geometry: Boolean = true, // Incluir geometría
    val instructions: Boolean = true, // Incluir instrucciones
    val language: String = "es", // Idioma
    val extra_info: List<String>? = listOf("tollways") // Información adicional, por ejemplo, peajes
)

data class AlternativeRoutes(
    val target_count: Int = 2, // Número de rutas alternativas
    val weight_factor: Double = 1.6 // Factor de peso para calcular alternativas
)