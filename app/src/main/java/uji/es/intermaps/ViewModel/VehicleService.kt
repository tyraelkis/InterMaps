package uji.es.intermaps.ViewModel

import uji.es.intermaps.Exceptions.NotSuchElementException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.Model.VehicleTypes

class VehicleService(private val repository: Repository) {
    //Igual hace falta crear un repositorio aparte como en InterestPlace
    suspend fun createVehicle(plate: String, type: String, consumption: Double): Vehicle {
        validateVehiclePlate(plate)
        validateVehicleType(type)
        validateVehicleConsumption(consumption)
        return repository.createVehicle(plate, type, consumption)
    }

    private fun validateVehiclePlate(plate: String) {
        val plateRegex = "^[0-9]{4}[A-Z]{3}$".toRegex()
        if (!plate.matches(plateRegex)) {
            throw IllegalArgumentException("La matrícula no tiene un formato válido.")
        }
    }

    private fun validateVehicleType(type: String) {
        val validTypes = listOf("gasolina", "diesel", "electrico")
        if (type.lowercase() !in validTypes) {
            throw IllegalArgumentException("El tipo de vehículo no es válido.")
        }
    }

    private fun validateVehicleConsumption(consumption: Double) {
        if (consumption <= 0) {
            throw IllegalArgumentException("La consumo debe ser un número positivo.")
        }
    }

    suspend fun deleteVehicle(plate: String): Boolean {
        return try {
            repository.deleteVehicle(plate)
        } catch (e: NotSuchElementException) {
            throw NotSuchElementException("No existe un vehículo con esa matrícula")
        }
    }

    suspend fun viewVehicleList(): List<Vehicle>{
        return repository.viewVehicleList()
    }

    suspend fun viewVehicleData(plate: String): Vehicle {
        return repository.viewVehicleData(plate)
    }

    suspend fun editVehicleData(plate: String, newType: String, newConsumption: Double): Boolean {
        if (newConsumption <= 0) {
            throw IllegalArgumentException("El consumo debe ser un número positivo.")
        }
        if (newType != VehicleTypes.GASOLINA.type && newType != VehicleTypes.DIESEL.type && newType != VehicleTypes.ELECTRICO.type) {
            throw IllegalArgumentException("El tipo de vehículo no es válido.")
        }
        if (!repository.editVehicleData(plate, newType, newConsumption)) {
            return false
        }
        return true
    }

    suspend fun setFavVehicle(plate: String): Boolean {
        return repository.setFavVehicle(plate)
    }

    suspend fun deleteFavVehicle(plate: String): Boolean {
        TODO()
    }
}