package com.example.myweather.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myweather.database.roomState.AlertRoomState
import com.example.myweather.model.AlertModel
import com.example.myweather.model.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertDialogFragmentViewModel (private val repoInterface: RepositoryInterface) : ViewModel() {

    private var alertMutableStateFlow = MutableStateFlow<Long>(0)
    val alertStateFlow = alertMutableStateFlow.asStateFlow()

    private var mutableStateFlow = MutableStateFlow<AlertRoomState>(AlertRoomState.Loading)
    val stateFlow = mutableStateFlow.asStateFlow()


    init {
        getStoredAlerts()
    }

    fun insertAlert(alert: AlertModel) {
        viewModelScope.launch(Dispatchers.IO) {
            alertMutableStateFlow.value = repoInterface.insertAlert(alert)
        }
    }

    fun deleteAlert(alert: AlertModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repoInterface.deleteAlert(alert)
        }
    }

    fun getStoredAlerts(){

        viewModelScope.launch(Dispatchers.IO) {
            repoInterface.getStoredAlerts()
                .catch {
                    mutableStateFlow.value = AlertRoomState.Failure(it)
                }.collect{
                    mutableStateFlow.value = AlertRoomState.Success(it)
                }
        }
    }
}


class AlertDialogViewModelFactory(private val repoInterface: RepositoryInterface)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return if (modelClass.isAssignableFrom(AlertDialogFragmentViewModel::class.java)) {
            AlertDialogFragmentViewModel(repoInterface) as T
        } else {
            throw java.lang.IllegalArgumentException("AlertFragmentViewModel class not found")
        }
    }
}