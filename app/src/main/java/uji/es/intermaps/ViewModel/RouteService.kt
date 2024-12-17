package uji.es.intermaps.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Exceptions.NotValidTransportException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes

open class RouteService( private val repository: Repository){
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