package uji.es.intermaps.ViewModel

import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.Exceptions.NoValidTypeException
import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Exceptions.NotValidTransportException
import uji.es.intermaps.Interfaces.ProxyService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes

open class RouteService(private val repository: Repository, private val servicioLuz: ProxyService){
    var routeRepository = RouteRepository(servicioLuz)

    suspend fun createRoute(origin: String, destination: String, transportMethod: TransportMethods, routeType: RouteTypes, vehiclePlate: String):Pair<Boolean,Route> {
        if (origin.isEmpty() or destination.isEmpty()){
            throw NotValidPlaceException()
        }
        if (origin == destination){
            throw NotValidPlaceException()
        }
        var originCoordinate = Coordinate(0.0, 0.0)
        var destinationCoordinate = Coordinate(0.0, 0.0)
        try{
            originCoordinate = repository.getInterestPlaceByToponym(origin).coordinate
        }catch (e: Exception){
            throw NotValidPlaceException()

        }

        try{
            destinationCoordinate = repository.getInterestPlaceByToponym(destination).coordinate
        }catch (e: Exception) {
            throw NotValidPlaceException()
        }
        val originString = "${originCoordinate.longitude},${originCoordinate.latitude}"
        val destinationString = "${destinationCoordinate.longitude},${destinationCoordinate.latitude}"
        val routeCall = createTypeRoute(originString, destinationString, transportMethod, routeType )
        val route = routeRepository.createRoute(origin, destination, transportMethod,routeType, vehiclePlate, routeCall.second)
        return Pair(true,route)
    }


    suspend fun deleteRoute(route: Route): Boolean {
        if (route.origin.isEmpty() or route.destination.isEmpty()){
            throw NotValidPlaceException()
        }
        if (route.origin == route.destination){
            throw NotValidPlaceException()
        }
        return repository.deleteRoute(route)
    }

    suspend fun calculateConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double {
        var res = 0.0
        if (transportMethod != TransportMethods.VEHICULO ){
            throw NotValidTransportException()
        } else {
            res = routeRepository.calculateConsumition(route, transportMethod, vehicleType)
        }
       return res
    }

    suspend fun calculateCaloriesConsumition(route: Route, transportMethod: TransportMethods ): Double {
        var res = 0.0
        if (transportMethod == TransportMethods.VEHICULO ){
            throw NotValidTransportException()
        } else {
            res = routeRepository.calculateCaloriesConsumition(route, transportMethod)
        }
        return res
    }

    /* No se usa
    suspend fun calculateConsumition1(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double {
        val res = routeRepository.calculateConsumition(route, transportMethod, vehicleType)
        return res
    }
    */

    suspend fun putRoute(route: Route): Boolean {
        if (route.origin.isEmpty() or route.destination.isEmpty()){
            throw NotValidPlaceException()
        }
        if (route.origin == route.destination){
            throw NotValidPlaceException()
        }
        repository.saveRouteToDatabase(route)
        return true

    }


    suspend fun putFuelCostAverage():Boolean {
        val cost: Boolean = routeRepository.calculateFuelCostAverage()
        return cost
    }

    /* Ya no se usa
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun putElectricityCost():Boolean {
        val cost: Boolean = routeRepository.calculateElectricityCost()
        return cost
    }
    */

    suspend fun getFuelCostAverage(): List<Double> {
        return repository.getAverageFuelPrices()
    }

    suspend fun getElctricCost():Double {
        return servicioLuz.getLightPrice()//TODO
        //return repository.getElectricPrice()//TODO
    }

    suspend fun getVehicleTypeAndConsump(route: Route): Pair<VehicleTypes, Double> {
        return repository.getVehicleTypeAndConsump(route)
    }
    suspend fun createTypeRoute(origin: String, destination: String, transportMethod: TransportMethods, routeType: RouteTypes?):Pair<Boolean,RouteFeature> {
        if (routeType == null){
            throw NoValidTypeException()
        }
        val res = routeRepository.calculateRoute(origin, destination, transportMethod, routeType)
        return Pair(true,res)
    }


    suspend fun viewRouteList(): List<Route> {
        return repository.viewRouteList()
    }

    suspend fun getRoute (origin: String, destination: String, transportMethod: TransportMethods, vehiclePlate: String): Route? {
        return routeRepository.getRoute(origin, destination, transportMethod, vehiclePlate)
    }

    suspend fun setFavRoute(origin: String, destination: String, transportMethod: TransportMethods, routeType: RouteTypes, vehiclePlate: String): Boolean {
        return repository.setFavRoute(origin, destination, transportMethod, routeType, vehiclePlate)
    }

    suspend fun deleteFavRoute(origin: String, destination: String, transportMethod: TransportMethods, routeType: RouteTypes, vehiclePlate: String): Boolean {
        return repository.deleteFavRoute(origin, destination, transportMethod, routeType, vehiclePlate)
    }

}