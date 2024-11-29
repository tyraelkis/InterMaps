package uji.es.intermaps.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uji.es.intermaps.Model.InterestPlace
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class InterestPlaceViewModel(
    private val interestPlaceService: InterestPlaceService
): ViewModel() {
    private val _interestPlace = MutableLiveData<InterestPlace>()

//    val interestPlace: LiveData<InterestPlace> get() = _interestPlace
//
//    init {
//        _interestPlace.value = InterestPlace()
//    }
//    fun setInterestPlace(place: InterestPlace){
//        _interestPlace.value = place
//    }
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


}