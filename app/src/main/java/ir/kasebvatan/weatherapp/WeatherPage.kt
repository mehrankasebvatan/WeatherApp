package ir.kasebvatan.weatherapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ir.kasebvatan.weatherapp.api.NetworkResponse
import ir.kasebvatan.weatherapp.model.WeatherModel
import ir.kasebvatan.weatherapp.ui.theme.WeatherAppTheme


@Composable
fun WeatherPage(viewModel: WeatherViewModel) {
    var city by rememberSaveable {
        mutableStateOf("")
    }

    val weather = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = city,
                onValueChange = { city = it },
                label = { Text("Search for any location") },
            )

            IconButton(onClick = {
                if (city.isNotEmpty()) {
                    viewModel.getData(city)
                    keyboardController?.hide()
                } else viewModel.emptyInput()
            }) {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        }

        when (val response = weather.value) {
            is NetworkResponse.Error -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(response.message)
                }
            }

            NetworkResponse.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier, strokeWidth = 4.dp
                    )
                }
            }

            is NetworkResponse.Success -> {
                WeatherDetail(response.data)
            }

            null -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("First Type your city name,\nthen click on search icon")
                }
            }

        }
    }


}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherDetail(data: WeatherModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        )
        {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "",
                Modifier.size(40.dp)
            )

            Text(data.location.name, fontSize = 30.sp)
            Text(" , ", fontSize = 30.sp)
            Text(data.location.country, fontSize = 18.sp)
        }
        Text(
            data.location.region, Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(32.dp))

        Text("${data.current.temp_c} °C", fontSize = 50.sp)

        GlideImage(
            model = "https:${data.current.condition.icon.replace("64x64", "128x128")}",
            contentDescription = "",
            modifier = Modifier.size(160.dp)
        )
        Text(data.current.condition.text, fontSize = 20.sp)
        Spacer(Modifier.height(32.dp))

        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                RowItem(
                    "Humidity",
                    data.current.humidity,
                    "Wind Speed",
                    data.current.wind_kph + " KM/H"
                )
                RowItem("UV", data.current.uv, "Participation", data.current.precip_mm)
                RowItem(
                    "Time",
                    data.location.localtime.split(" ")[1],
                    "Date",
                    data.location.localtime.split(" ")[0]
                )
            }
        }

    }
}

@Composable
fun RowItem(t: String, t1: String, t2: String, t3: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(t, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(t1)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)

        ) {
            Text(t2, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(t3)
        }
    }
    Spacer(Modifier.height(16.dp))
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = true,
    showBackground = true
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun WeatherPagePreview() {
    WeatherAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            WeatherPage(WeatherViewModel())
        }
    }
}