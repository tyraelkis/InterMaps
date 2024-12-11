package uji.es.intermaps.ViewModel

import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes

open class RouteService(private val repository: Repository){
    var routeRepository = RouteRepository()

    suspend fun createRoute(origin: String, destination: String, trasnportMethod: TransportMethods):Route {
        if (origin.isEmpty() or destination.isEmpty()){
            throw NotValidPlaceException()
        }
        return repository.createRoute(origin, destination, trasnportMethod)
    }

    fun deleteRoute(origin: String,destination: String, trasnportMethod: TransportMethods): Boolean {
        TODO()
    }

    fun calculateFuelConsumition(origin: String,destination: String, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double {
        TODO()
    }

    fun calculateCaloriesConsumition(origin: String,destination: String, transportMethod: TransportMethods,): Double {
        TODO()
    }

}