package uji.es.intermaps.ViewModel

import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Vehicle

class VehicleService(private val repository: Repository) {
    //Igual hace falta crear un repositorio aparte como en InterestPlace
    suspend fun createVehicle(plate: String, model: String, consumption: Double): Vehicle {
        TODO()
    }

    suspend fun deleteVehicle(plate: String): Boolean {
        TODO()
    }

    suspend fun viewVehicleList(): List<Vehicle>{
        TODO()
    }
}