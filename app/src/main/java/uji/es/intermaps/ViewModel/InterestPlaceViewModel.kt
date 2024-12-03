package uji.es.intermaps.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uji.es.intermaps.Model.InterestPlace
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uji.es.intermaps.APIParsers.PossibleCoord
import uji.es.intermaps.Interfaces.ORSAPI


class InterestPlaceViewModel(
    private val interestPlaceService: InterestPlaceService
): ViewModel() {
    private val _locations = mutableStateOf<List<PossibleCoord>>(emptyList())
    val locations: State<List<PossibleCoord>> = _locations


    // Retrofit setup
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val _interestPlace = MutableLiveData<InterestPlace>()

    private val openRouteService = retrofit.create(ORSAPI::class.java)

    init {
        _interestPlace.value = InterestPlace()
    }
    fun updateInterestPlace(place: InterestPlace){
        _interestPlace.value = place
    }
    var loading by mutableStateOf(false)
        private set
    var interestPlace by mutableStateOf(InterestPlace())
        private set

    fun putInterestPlace(interestPlace: InterestPlace) {
        this.interestPlace = interestPlace
    }

    suspend fun getInterestPlaceByToponym(toponym: String) {
        loading = true
        viewModelScope.launch {
            interestPlaceService.getInterestPlaceByToponym(toponym) {
                if (it.isNotEmpty()) {
                    this@InterestPlaceViewModel.interestPlace = it.first()
                    loading = false
                }
            }
        }

    }

    fun getLocationsByToponim(toponym: String){
        viewModelScope.launch {
            try {
                val response = openRouteService.getCoordinatesFromToponym(
                    "5b3ce3597851110001cf6248d49685f8848445039a3bcb7f0da42f23",
                    toponym
                )

                val places= response.features
                if (places.isNotEmpty()){
                    _locations.value = places
                }
            }catch (e: Exception){
                Log.e("Locations", "Error: ${e.message}")
            }
        }

    }
}