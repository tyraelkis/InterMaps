package uji.es.intermaps.ViewModel

import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TrasnportMethods

open class RouteService(private val repository: Repository){
    public var routeRepository = RouteRepository()

    suspend fun createRoute(origin: String, destination: String, trasnportMethod: TrasnportMethods):Route {
        TODO()
    }

    fun deleteRoute(origin: String,destination: String, trasnportMethod: TrasnportMethods): Boolean {
        TODO()
    }

    fun calculateFuelConsumition(origin: String,destination: String, vehiclePlate: String): Double {
        TODO()
    }

    fun calculateCaloriesConsumition(origin: String,destination: String): Double {
        TODO()
    }

}