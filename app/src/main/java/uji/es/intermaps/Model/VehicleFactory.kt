package uji.es.intermaps.Model

object VehicleFactory {
    fun createVehicle(plate: String, type: String, consumption: Double, fav: Boolean): Vehicle {
        return when (type.lowercase()) {
            "electrico" -> ElectricVehicle(plate, type, consumption, fav)
            "gasolina" -> GasolineVehicle(plate, type, consumption, fav)
            "diesel" -> DieselVehicle(plate, type, consumption, fav)
            else -> throw IllegalArgumentException("No se reconoce el tipo de vehículo: $type")
        }
    }

    fun cloneWithFav(vehicle: Vehicle, fav: Boolean): Vehicle {
        return when (vehicle) {
            is ElectricVehicle -> ElectricVehicle(vehicle.plate, vehicle.type, vehicle.consumption, fav)
            is GasolineVehicle -> GasolineVehicle(vehicle.plate, vehicle.type, vehicle.consumption, fav)
            is DieselVehicle -> DieselVehicle(vehicle.plate, vehicle.type, vehicle.consumption, fav)
            else -> throw IllegalArgumentException("No se reconoce el tipo de vehículo: ${vehicle.type}")
        }
    }
}