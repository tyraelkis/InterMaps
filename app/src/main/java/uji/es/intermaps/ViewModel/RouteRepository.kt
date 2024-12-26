package uji.es.intermaps.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.mapbox.geojson.utils.PolylineUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.APIParsers.RouteRequestBody
import uji.es.intermaps.APIParsers.RouteResponse
import uji.es.intermaps.APIParsers.RouteSummary
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Exceptions.NotValidTransportException
import uji.es.intermaps.Interfaces.ElectricityPriceRepository
import uji.es.intermaps.Interfaces.FuelPriceRepository
import uji.es.intermaps.Interfaces.ORSRepository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase.db
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.RetrofitConfig
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class RouteRepository (): ORSRepository, FuelPriceRepository, ElectricityPriceRepository {
    private val apiKey = "5b3ce3597851110001cf6248d49685f8848445039a3bcb7f0da42f23"
    val openRouteService = RetrofitConfig.createRetrofitOpenRouteService()
    override suspend fun searchInterestPlaceByCoordinates(coordinate: Coordinate): InterestPlace {
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        val openRouteService = RetrofitConfig.createRetrofitOpenRouteService()
        var toponym = ""

        //Llamada a la API
        val response = withContext(Dispatchers.IO) {
            openRouteService.getToponym(
                apiKey,
                coordinate.longitude,
                coordinate.latitude
            ).execute()
        }
        if (response.isSuccessful) {
            response.body()?.let { ORSResponse ->
                val respuesta = ORSResponse.features
                if (respuesta.isNotEmpty()) {
                    toponym = respuesta[0].properties.label
                }
            }
        } else {
            throw Exception("Error en la llamada a la API")
        }
        return InterestPlace(coordinate, toponym, "", false)
    }

    override suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace {
        val openRouteService = RetrofitConfig.createRetrofitOpenRouteService()
        val coordinate: Coordinate

        try {
            // Llamada a la API para obtener las coordenadas del topónimo
            val response = openRouteService.getCoordinatesFromToponym(
                apiKey,
                toponym
            )
            val respuesta = response.features
            if (respuesta.isNotEmpty()) {
                val feature = respuesta[0]
                val lon = feature.geometry.coordinates[0]
                val lat = feature.geometry.coordinates[1]

                // Validamos las coordenadas
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    throw NotValidCoordinatesException("Las coordenadas no son válidas")
                }

                coordinate = Coordinate(lat,lon)
                return InterestPlace(coordinate, toponym, "", false)
            }

        } catch (e: Exception) {
            // Manejo de excepciones
            Log.e("Coordenadas", "Error: ${e.message}")
        }

        throw NotSuchPlaceException("Error en la llamada a la API para obtener las coordenadas")
    }

    override suspend fun calculateRoute(origin: String, destination: String, transportMethod: TransportMethods,routeType: RouteTypes) : RouteFeature {
        var route = RouteFeature(geometry ="",
            summary = RouteSummary(distance = 0.0, duration = 0.0)
        )
        val originCoordinate = parseCoordinates(origin)
        val destinationCoordinate = parseCoordinates(destination)
        try {
            val routeTypePreference = when (routeType) {
                RouteTypes.RAPIDA -> "fastest"
                RouteTypes.CORTA -> "shortest"
                RouteTypes.ECONOMICA -> "recommended"
            }

            val avoidPeajes = if (transportMethod == TransportMethods.VEHICULO && routeType == RouteTypes.ECONOMICA) {
                "tollways" //
            } else {
                null
            }

            val call = when (transportMethod) {
                TransportMethods.VEHICULO -> openRouteService.calculateRouteVehicle(
                    apiKey,
                    requestBody =  RouteRequestBody(
                        coordinates = listOf(originCoordinate, destinationCoordinate),
                        extra_info = listOfNotNull(avoidPeajes),
                        preference = routeTypePreference
                    )

                )
                TransportMethods.BICICLETA -> openRouteService.calculateRouteBycicle(
                    apiKey,
                    origin,
                    destination,
                    routeTypePreference
                )
                TransportMethods.APIE -> openRouteService.calculateRouteWalk(
                    apiKey,
                    origin,
                    destination,
                    routeTypePreference
                )
            }


            if (call.isSuccessful) {
                Log.d("createRoute", "Ruta creada exitosamente")
                route = call.body()!!.routes[0]

            } else {
                Log.e("createRoute", "Error al crear la ruta: ${call.message()}")
            }
        }catch (e:Exception){
            Log.e("createRoute", "Error al crear la ruta: ${e.message}")

        }
        return route
    }

    override suspend fun calculateFuelConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double {
        val repository = FirebaseRepository()
        val routeService = RouteService(repository)
        var coste = 0.0
        val consumoMedioGasolina95_l_por_100km = 7.0
        val consumoMedioGasoleoA_l_por_100km = 5.0
        if (transportMethod == TransportMethods.VEHICULO && vehicleType == VehicleTypes.GASOLINA ){
            coste = (route.distance/100) * consumoMedioGasolina95_l_por_100km * routeService.getFuelCostAverage().get(0)
        }
        if (transportMethod == TransportMethods.VEHICULO && vehicleType == VehicleTypes.DIESEL){
            coste = (route.distance/100) * consumoMedioGasoleoA_l_por_100km * routeService.getFuelCostAverage().get(1)
        }
        return coste
    }

    override suspend fun calculateElectricConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double {
        val repository = FirebaseRepository()
        val routeService = RouteService(repository)
        var coste = 0.0
        val consumoMediokWh_por_100km = 7.0
        if (transportMethod == TransportMethods.VEHICULO && vehicleType == VehicleTypes.ELECTRICO ){
            coste = (route.distance/100) * consumoMediokWh_por_100km * (routeService.getElctricCost()/1000)
        }
        return coste
    }


    override suspend fun calculateCaloriesConsumition(route: Route, transportMethod: TransportMethods): Double {
        var coste = 0.0
        val caloriasMediaBici = 45
        val caloriasMediaCaminar = 62
        if (transportMethod == TransportMethods.APIE){
            coste = (route.distance/100) * caloriasMediaCaminar
        }
        if (transportMethod == TransportMethods.BICICLETA){
            coste = (route.distance/100) * caloriasMediaBici
        }
        return coste
    }

    override suspend fun calculateFuelCostAverage(): Boolean {
        val openRouteService = RetrofitConfig.createRetrofitFuelPrice()

        val list : List<Double>
        return try {
            val response = openRouteService.getFuelCostAverage()
            val fuelStations = response.listaEstaciones
            var gasolina95Total = 0.0
            var gasoleoATotal = 0.0
            for (fuelStation in fuelStations) {
                val gasolina95 = fuelStation.gasolina95?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                val gasoleoA = fuelStation.gasoleoA?.replace(",", ".")?.toDoubleOrNull() ?:0.0
                gasolina95Total += gasolina95
                gasoleoATotal += gasoleoA
            }

            val numStations = fuelStations.size
            val mediaGasolina95 = if (numStations > 0) gasolina95Total / numStations else 0.0
            val mediaGasoleoA = if (numStations > 0) gasoleoATotal / numStations else 0.0

            val mediaGasolina95Rounded = BigDecimal(mediaGasolina95).setScale(3, RoundingMode.HALF_UP).toDouble()
            val mediaGasoleoARounded = BigDecimal(mediaGasoleoA).setScale(3, RoundingMode.HALF_UP).toDouble()

            list = listOf(mediaGasolina95Rounded, mediaGasoleoARounded)
            saveFuelCostAverageToDatabase(list)
            return true
        } catch (e: Exception) {
            Log.e("FuelAPI", "Error al obtener las estaciones de servicio: ${e.message}")
            return false
        }

    }

    private fun saveFuelCostAverageToDatabase(averages: List<Double>) {
        val fuelPricesDocument = db.collection("FuelPrices").document("mediaPrecios")
        fuelPricesDocument.set(
            mapOf(
                "gasolina95" to averages[0],
                "gasoleoA" to averages[1],
                "timestamp" to System.currentTimeMillis()
            )
        )
    }

    private fun saveElectricityCostToDatabase(precio: Double){
        val electricityPricesDocument = db.collection("ElectricityPrices").document("precios")
        electricityPricesDocument.set(
            mapOf(
                "precioLuz" to precio,
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun calculateElectricityCost(): Boolean {
        val openRouteService = RetrofitConfig.createRetrofitPrecioLuz()

        return try {
            val dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

            val jsonFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

            val now = LocalDateTime.now()
            val currentHour = now.hour

            val today = LocalDate.now()
            val startDate = today.atStartOfDay().format(dateformatter)
            val endDate = today.atTime(23, 59).format(dateformatter)

            val response = openRouteService.getElectricityCost(startDate, endDate, "hour")

            val pvpctData = response.included
                    .filter { it.type == "PVPC" }
                .flatMap { it.attributes.values }

            val matchingEntry = pvpctData.firstOrNull { entry ->
                val entryHour = LocalDateTime.parse(entry.datetime, jsonFormatter).hour
                entryHour == currentHour
            }

            matchingEntry?.let {
                saveElectricityCostToDatabase(it.value)
            } ?: Log.w("ElectricityCost", "No se encontró un precio para la hora actual.")

            return true
        } catch (e: Exception) {
            Log.e("FuelAPI", "Error al obtener las estaciones de servicio: ${e.message}")
            return false
        }
    }
    fun parseCoordinates(coordinateString: String): List<Double> {

        val parts = coordinateString.split(",")

        return listOf(parts[0].toDouble(), parts[1].toDouble())
    }

}