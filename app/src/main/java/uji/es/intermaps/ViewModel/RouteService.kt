package uji.es.intermaps.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        val route = repository.createRoute(origin, destination, transportMethod,routeType, vehiclePlate,routeCall)
        return route
    }


    fun deleteRoute(origin: String,destination: String, trasnportMethod: TransportMethods): Boolean {
        TODO()
    }

    suspend fun calculateFuelConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Boolean {
        var res :Boolean
        val routeRepository = RouteRepository()
        if (transportMethod != TransportMethods.VEHICULO || vehicleType == VehicleTypes.ELECTRICO ){
            throw NotValidTransportException()
        } else {
            routeRepository.calculateFuelConsumition(route, transportMethod, vehicleType,)
            res = true
        }
       return res
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun calculateElectricConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Boolean {
        var res: Boolean
        val routeRepository = RouteRepository()
        if (transportMethod != TransportMethods.VEHICULO || vehicleType != VehicleTypes.ELECTRICO ){
            throw NotValidTransportException()
        } else {
            routeRepository.calculateElectricConsumition(route, transportMethod, vehicleType,)
            res = true
        }
        return res
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





}