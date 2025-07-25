package ir.kasebvatan.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.kasebvatan.weatherapp.api.Constant.API_KEY
import ir.kasebvatan.weatherapp.api.NetworkResponse
import ir.kasebvatan.weatherapp.api.RetrofitInstance
import ir.kasebvatan.weatherapp.model.WeatherModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>?>()
    val weatherResult: MutableLiveData<NetworkResponse<WeatherModel>?> = _weatherResult


    fun getData(cityName: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            //delay(1000)
            try {
                val response = weatherApi.getWeather(API_KEY, cityName)
                if (response.isSuccessful) response.body()?.let {
                    _weatherResult.value = NetworkResponse.Success(it)
                }
                else _weatherResult.value = NetworkResponse.Error("Failed to load data!")
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Failed to load data!")
            }
        }

    }

    fun emptyInput(){
        _weatherResult.value = null
    }

}