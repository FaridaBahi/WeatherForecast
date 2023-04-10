package com.example.myweather.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myweather.database.roomState.AlertRoomState
import com.example.myweather.model.AlertModel
import com.example.myweather.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel (private val repoInterface: RepositoryInterface) : ViewModel() {

    private var _alertStateFlow: MutableStateFlow<AlertRoomState> = MutableStateFlow(AlertRoomState.Loading)
    var alertStateFlow: StateFlow<AlertRoomState> = _alertStateFlow


    init {
        getStoredAlerts()
    }

    private fun getStoredAlerts(){

        viewModelScope.launch(Dispatchers.IO) {
            repoInterface.getStoredAlerts()
                .catch {
                    _alertStateFlow.value = AlertRoomState.Failure(it)
                }.collect{
                    _alertStateFlow.value = AlertRoomState.Success(it)
                }
        }
    }

    fun deleteAlert(alert: AlertModel){
        viewModelScope.launch(Dispatchers.IO) {
            repoInterface.deleteAlert(alert)
        }
    }
}


class AlertViewModelFactory(private val repoInterface: RepositoryInterface)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            AlertViewModel(repoInterface) as T
        } else {
            throw java.lang.IllegalArgumentException("Alert ViewModel class is not found")
        }
    }
}