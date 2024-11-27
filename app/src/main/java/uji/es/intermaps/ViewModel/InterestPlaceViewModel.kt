package uji.es.intermaps.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uji.es.intermaps.Model.InterestPlace
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class InterestPlaceViewModel: ViewModel() {
    private val _interestPlace = MutableLiveData<InterestPlace>()

    val interestPlace: LiveData<InterestPlace> get() = _interestPlace

    init {
        _interestPlace.value = InterestPlace()
    }
    fun setInterestPlace(place: InterestPlace){
        _interestPlace.value = place
    }
}