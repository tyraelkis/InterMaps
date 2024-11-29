package uji.es.intermaps.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uji.es.intermaps.Model.InterestPlace
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(
    private val userService: UserService
): ViewModel() {
    private val _user = MutableLiveData<InterestPlace>()

    var loading by mutableStateOf(false)
        private set



}