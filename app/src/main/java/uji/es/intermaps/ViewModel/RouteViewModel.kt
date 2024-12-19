package uji.es.intermaps.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import kotlinx.coroutines.launch
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods

class RouteViewModel(private val routeService: RouteService): ViewModel() {

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route


    var loading by mutableStateOf(false)
        private set


    fun updateRoute(origin: String, destination: String, trasnportMethod: TransportMethods, routeType: RouteTypes, vehicle: String){
        loading = true
        viewModelScope.launch {
            val route = routeService.createRoute(origin,destination,trasnportMethod,routeType,vehicle)
            _route.value = route
            loading = false
        }

    }

}