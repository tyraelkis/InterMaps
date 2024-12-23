package uji.es.intermaps.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods

class RouteViewModel(private val routeService: RouteService): ViewModel() {
    val repository = FirebaseRepository()

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    var loading by mutableStateOf(false)
        private set


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRoute( origin: String, destination: String, transportMethod: TransportMethods,
        routeType: RouteTypes, vehicle: String
    ) {
        loading = true
        viewModelScope.launch {
            try {
                val route = routeService.createRoute(origin, destination, transportMethod, routeType, vehicle)
                _route.value = route
                val currentRoute = _route.value ?: throw IllegalStateException("No hay una ruta cargada")
                var cost = 0.0
                if (transportMethod == TransportMethods.VEHICULO) {
                    val vehicleType = routeService.getVehicleTypeAndConsump(currentRoute).first
                    cost = routeService.calculateConsumition( currentRoute, currentRoute.trasnportMethod, vehicleType)
                } else{
                    cost = routeService.calculateCaloriesConsumition(currentRoute, currentRoute.trasnportMethod)
                }
                route.cost = cost
            } catch (e: Exception) {
                Log.e("RouteViewModel", "Error al actualizar la ruta: ${e.message}")
            } finally {
                loading = false
            }
        }
    }


}