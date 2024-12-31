package uji.es.intermaps.ViewModel

import androidx.lifecycle.MutableLiveData
import uji.es.intermaps.Exceptions.NotSuchElementException
import uji.es.intermaps.Exceptions.VehicleAlreadyExistsException
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.Model.VehicleFactory
import uji.es.intermaps.Model.VehicleTypes

class VehicleViewModel {
    private val vehicleFactory = VehicleFactory
    private val repository = FirebaseRepository()
    private val vehicleService = VehicleService(repository)

    private var showPopupEditSucces: Boolean = false
    private var showPopupCreateSucces: Boolean = false
    private var errorMessage: String = ""

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

    suspend fun getVehicleData(plate: String): Vehicle {
        return vehicleService.viewVehicleData(plate)
    }

    suspend fun editVehicleData(plate: String, type: String, consumption: Double) {
        try {
            vehicleService.editVehicleData(plate, type, consumption)
            showPopupEditSucces = true
            errorMessage = ""
        } catch (e: IllegalArgumentException) {
            errorMessage = e.message.toString()
        } catch (e: IllegalStateException) {
            errorMessage = e.message.toString()
        }catch (e: NotSuchElementException) {
            errorMessage = e.message.toString()
        }catch (e: Exception) {
            errorMessage = e.message.toString()
        }
    }

    suspend fun deleteVehicle(plate: String){
        try {
            vehicleService.deleteVehicle(plate)
            errorMessage = ""
        } catch (e: IllegalArgumentException) {
            errorMessage = e.message.toString()
        } catch (e: VehicleAlreadyExistsException) {
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
    }

    suspend fun viewVehicleList(): List<Vehicle> {
        try {
            val vehicles = vehicleService.viewVehicleList()
             return vehicles
        } catch (e: Exception) {
            return emptyList()
        }
    }

    suspend fun createVehicle(plate: String, type: String, consumption: Double) {
        try {
            vehicleService.createVehicle(plate, type, consumption)
            showPopupCreateSucces = true
            errorMessage = ""
        } catch (e: IllegalArgumentException) {
            errorMessage = e.message.toString()
        } catch (e: VehicleAlreadyExistsException) {
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
    }

    fun getShowPopupEditSucces(): Boolean {
        return showPopupEditSucces
    }

    fun getShowPopupCreateSucces(): Boolean {
        return showPopupCreateSucces
    }

    fun getErrorMessage(): String {
        return errorMessage
    }

    suspend fun setFavVehicle(plate: String): Boolean{
        try {
            return vehicleService.setFavVehicle(plate)
        }
        catch (e: Exception){
            return false
        }
    }

    suspend fun deleteFavVehicle(plate: String): Boolean{
        try {
            return vehicleService.deleteFavVehicle(plate)
        }
        catch (e: Exception){
            return false
        }
    }

    fun cloneWithFav(vehicle: Vehicle, fav: Boolean): Vehicle {
        return vehicleFactory.cloneWithFav(vehicle, fav)
    }
}