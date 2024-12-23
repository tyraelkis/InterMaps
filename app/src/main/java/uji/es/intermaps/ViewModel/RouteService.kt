package uji.es.intermaps.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uji.es.intermaps.APIParsers.RouteGeometry
import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Exceptions.NotValidTransportException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes

open class RouteService(private val repository: Repository){
    public var routeRepository = RouteRepository()

    suspend fun createRoute(origin: String, destination: String, transportMethod: TransportMethods, routeType: RouteTypes, vehiclePlate: String = ""):Route {
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
        val routeCall = routeRepository.calculateRoute(originString, destinationString, transportMethod, routeType )
        val route = routeRepository.createRoute(origin, destination, transportMethod,routeType, vehiclePlate, routeCall)
        return route
    }


    fun deleteRoute(origin: String,destination: String, trasnportMethod: TransportMethods): Boolean {
        TODO()
    }

    suspend fun calculateConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double {
        var res = 0.0
        if (transportMethod != TransportMethods.VEHICULO ){
            throw NotValidTransportException()
        } else {
            res = routeRepository.calculateConsumition(route, transportMethod, vehicleType,)
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
        val routeRepository = RouteRepository()
        val cost: Boolean = routeRepository.calculateFuelCostAverage()
        return cost
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun putElectricityCost():Boolean {
        val routeRepository = RouteRepository()
        val cost: Boolean = routeRepository.calculateElectricityCost()
        return cost
    }

    suspend fun getFuelCostAverage(): List<Double> {
        return repository.getAverageFuelPrices()
    }

    suspend fun getElctricCost():Double {
        return repository.getElectricPrice()
    }

    suspend fun getVehicleTypeAndConsump(route: Route): Pair<VehicleTypes, Double> {
        return repository.getVehicleTypeAndConsump(route)
    }

    fun convertToCoordinate(geometry: RouteGeometry): List<Coordinate> {
        return repository.convertToCoordinate(geometry)

    }



}