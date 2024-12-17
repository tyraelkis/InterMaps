package uji.es.intermaps.ViewModel

import androidx.lifecycle.MutableLiveData
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.Model.VehicleFactory
import uji.es.intermaps.Model.VehicleTypes

class VehicleViewModel {
    private val vehicleFactory = VehicleFactory
    private val _vehicle = MutableLiveData<Vehicle>()

    init {
        _vehicle.value = vehicleFactory.createVehicle("0000ZZZ", VehicleTypes.ELECTRICO.type, 0.0, false)
    }
    fun updateVehicle(vehicle: Vehicle){
        _vehicle.value = vehicle
    }
    fun getVehicle(): Vehicle {
        return vehicleFactory.createVehicle(_vehicle.value!!.plate, _vehicle.value!!.type, _vehicle.value!!.consumption, _vehicle.value!!.fav)
    }
}