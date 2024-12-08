package uji.es.intermaps.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uji.es.intermaps.Model.InterestPlace
import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uji.es.intermaps.APIParsers.PossibleCoord
import uji.es.intermaps.Exceptions.UnableToDeletePlaceException
import uji.es.intermaps.Interfaces.ORSAPI
import uji.es.intermaps.Model.Coordinate


class InterestPlaceViewModel(
    private val interestPlaceService: InterestPlaceService
): ViewModel() {
    private val _locations = mutableStateOf<List<PossibleCoord>>(emptyList())
    val locations: State<List<PossibleCoord>> = _locations

    private val _showDeletePopUp = mutableStateOf(false)
    val showDeleteDialog: State<Boolean> get() = _showDeletePopUp

    private val _showUpdatePopUp = mutableStateOf(false)
    val showUpdateDialog: State<Boolean> get() = _showUpdatePopUp

    var coordinate by mutableStateOf(Coordinate(0.0, 0.0))

    // Retrofit setup
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val _interestPlace = MutableLiveData<InterestPlace>()

    private val openRouteService = retrofit.create(ORSAPI::class.java)

    private val _showCreateInterestPlaceCorrectPopUp = mutableStateOf(false)
    val showCreateInterestPlaceCorrectPopUp: State<Boolean> get() = _showCreateInterestPlaceCorrectPopUp

    private val _showCreateInterestPlaceErrorPopUp = mutableStateOf(false)
    val showCreateInterestPlaceErrorPopUp: State<Boolean> get() = _showCreateInterestPlaceErrorPopUp

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
    var newAlias by mutableStateOf("")

    fun putInterestPlace(interestPlace: InterestPlace) {
        this.interestPlace = interestPlace
    }

    fun showDeleteInterestPlacePopUp(){
        _showDeletePopUp.value = true
    }

    fun hideDeleteInterestPlacePopUp(){
        _showDeletePopUp.value = false
    }

    fun showUpdateInterestPlacePopUp(){
        _showDeletePopUp.value = true
    }

    fun hideUpdateInterestPlacePopUp(){
        _showDeletePopUp.value = false
    }

    fun showCreateInterestPlaceCorrectPopUp(){
        _showCreateInterestPlaceCorrectPopUp.value = true
    }
    fun hideCreateInterestPlaceCorrectPopUp(){
        _showCreateInterestPlaceCorrectPopUp.value = false
    }

    fun showCreateInterestPlaceErrorPopUp(){
        _showCreateInterestPlaceErrorPopUp.value = true
    }
    fun hideCreateInterestPlaceErrorPopUp() {
        _showCreateInterestPlaceErrorPopUp.value = false
    }

    fun getInterestPlaceByToponym(toponym: String) {
        loading = true
        viewModelScope.launch {
            interestPlace = interestPlaceService.getInterestPlaceByToponym(toponym)
            Log.i("interestplace", interestPlace.toString())
            if (interestPlace!= null){
                loading = false
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

    //Variable u función para pasarle el lugar de interés a la pestaña de crear lugar
    var selectedInterestPlace by mutableStateOf(InterestPlace())
        private set

    fun setInterestPlaceForCreation(interestPlace: InterestPlace) {
        selectedInterestPlace = interestPlace
    }

    suspend fun deleteInterestPlace (coordinate: Coordinate){
        try {
            interestPlaceService.deleteInterestPlace(coordinate)

        }catch (e: UnableToDeletePlaceException){}
    }
}