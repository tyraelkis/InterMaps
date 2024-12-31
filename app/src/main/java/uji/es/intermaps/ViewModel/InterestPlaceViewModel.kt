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
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
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

    private val _showUpdateInterestPlacePopUp = mutableStateOf(false)
    val showUpdateInterestPlacePopUp: State<Boolean> get() = _showUpdateInterestPlacePopUp

    private val _showUpdateInterestPlaceErrorPopUp = mutableStateOf(false)
    val showUpdateInterestPlaceErrorPopUp: State<Boolean> get() = _showUpdateInterestPlaceErrorPopUp

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

    fun showDeleteInterestPlacePopUp(){
        _showDeletePopUp.value = true
    }

    fun hideDeleteInterestPlacePopUp(){
        _showDeletePopUp.value = false
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

    fun showUpdateInterestPlacePopUp(){
        _showUpdateInterestPlacePopUp.value = true
    }

    fun hideUpdateInterestPlacePopUp(){
        _showUpdateInterestPlacePopUp.value = false
    }

    fun showUpdateInterestPlaceErrorPopUp(){
        _showUpdateInterestPlaceErrorPopUp.value = true
    }
    fun hideUpdateInterestPlaceErrorPopUp() {
        _showUpdateInterestPlaceErrorPopUp.value = false
    }

    fun getInterestPlaceByToponym(toponym: String) {
        loading = true
        viewModelScope.launch {
            interestPlace = interestPlaceService.getInterestPlaceByToponym(toponym)
            if (interestPlace != null){
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
            errorMessage = ""
            updateErrorMessage("")
        } catch (e: UnableToDeletePlaceException){
            errorMessage = e.message.toString()
            updateErrorMessage(e.message.toString())
        } catch (e: Exception){
            errorMessage = e.message.toString()
            updateErrorMessage(e.message.toString())
        }
    }


    //Lo de arriba es un lío asi que hago las cosas a partir de aqui :)
    private var errorMessage = ""

    suspend fun gestorDeBusqueda(busqueda: String): InterestPlace{
        val coordinatePattern = Regex("""^\s*(-?\d+(\.\d+)?)\s*,\s*(-?\d+(\.\d+)?)\s*$""")
        var coordenada = Coordinate()
        var busquedaCoordenadas = false
        val input = busqueda.trim()
        if (coordinatePattern.matches(input)) {
            val parts = input.split(",")
            val lat = parts[0].trim().toDouble()
            val lon = parts[1].trim().toDouble()
            coordenada = Coordinate(lat, lon)
            busquedaCoordenadas = true
        }
        if (busquedaCoordenadas)
            return searchInterestPlaceByCoordiante(coordenada)
        else
            return searchInterestPlaceByToponym(input)
    }

    suspend fun searchInterestPlaceByCoordiante(coordinate: Coordinate): InterestPlace{
        var iP = InterestPlace()
        try {
            iP = interestPlaceService.searchInterestPlaceByCoordiante(coordinate)
            errorMessage = ""
        } catch (e: NotValidCoordinatesException) {
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
        return iP
    }

    suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace{
        var iP = InterestPlace()
        try {
            iP = interestPlaceService.searchInterestPlaceByToponym(toponym)
            errorMessage = ""
        } catch (e: NotSuchPlaceException) {
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
        return iP
    }

    suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String){
        try {
            interestPlaceService.createInterestPlace(coordinate, toponym, alias)
            errorMessage = ""
        } catch (e: NotValidCoordinatesException) {
            errorMessage = e.message.toString()
        } catch (e: NotValidAliasException) {
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
    }

    suspend fun createInterestPlaceFromToponym(toponym: String){
        try {
            interestPlaceService.createInterestPlaceFromToponym(toponym)
            errorMessage = ""
        } catch (e: NotSuchPlaceException) {
            errorMessage = e.message.toString()
        } catch (e: Exception) {
            errorMessage = e.message.toString()
        }
    }

    suspend fun setAlias(interestPlace: InterestPlace, newAlias: String): Boolean{
        try {
            interestPlaceService.setAlias(interestPlace, newAlias)
            return true
        }catch (e: Exception){
            return false
        }
    }

    fun getErrorMessage(): String{
        return errorMessage
    }

    suspend fun setFavInterestPlace(coordinate: Coordinate): Boolean{
        try {
            return interestPlaceService.setFavInterestPlace(coordinate)
        }
        catch (e: Exception){
            return false
        }
    }

    suspend fun deleteFavInterestPlace(coordinate: Coordinate): Boolean{
        try {
            return interestPlaceService.deleteFavInterestPlace(coordinate)
        }
        catch (e: Exception){
            return false
        }
    }

    private val _errorM = mutableStateOf("")
    val errorM: State<String> = _errorM

    fun updateErrorMessage(message: String) {
        _errorM.value = message
    }
}