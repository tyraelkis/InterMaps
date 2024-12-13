package uji.es.intermaps.ViewModel

import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TrasnportMethods

open class RouteService(private val repository: Repository){
    public var routeRepository = RouteRepository()

    suspend fun createRoute(origin: String, destination: String, trasnportMethod: TrasnportMethods):Route {
        if (origin.isEmpty() or destination.isEmpty()){
            throw NotValidPlaceException()
        }
        if (origin == destination){
            throw NotValidPlaceException()
        }
        val originCoordinate = routeRepository.searchInterestPlaceByToponym(origin).coordinate
        val destinationCoordinate = routeRepository.searchInterestPlaceByToponym(destination).coordinate
        val originString = "${originCoordinate.longitude},${originCoordinate.latitude}"
        val destinationString = "${destinationCoordinate.longitude},${destinationCoordinate.latitude}"
        val route = routeRepository.calculateRoute(originString, destinationString)
        val res = repository.createRoute(route)
        return res
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