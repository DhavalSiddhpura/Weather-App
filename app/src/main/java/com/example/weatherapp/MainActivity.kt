package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Tag
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("Rajkot")
        searchCity()
    }

    private fun searchCity() {
        val search_view = binding.searchView
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityname:String) {
         val retrofit = Retrofit.Builder()
             .baseUrl("https://api.openweathermap.org/")
             .addConverterFactory(GsonConverterFactory.create())
             .build().create(ApiInterface::class.java)
         val response = retrofit.getWeatherData(cityname,"cee91d4bfc6172cae8531d582a66f3fd","metric")
         response.enqueue(object : Callback<WeatherApp>{
             override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                 val responseBody = response.body()
                 if (response.isSuccessful && responseBody != null){
                     val temperature = responseBody.main.temp.toString()
                     Log.e("TAG","$temperature")
                     val humidity = responseBody.main.humidity
                     val windSpeed = responseBody.wind.speed
                     val sunRise = responseBody.sys.sunrise.toLong()
                     val sunSet = responseBody.sys.sunset.toLong()
                     val seaLevel = responseBody.main.pressure
                     val condition = responseBody.weather.firstOrNull()?.main?: "unknown"



                     binding.temp.text = "${temperature} °C"
                     binding.humidity.text = "${humidity}%"
                     binding.windspeed.text = "${windSpeed} m/s"
                     binding.sunrise.text = "${time(sunRise)}"
                     binding.sunset.text = "${time(sunSet)}"
                     binding.sea.text = "${seaLevel} hPa"
                     binding.condition.text = "${condition}"

                     binding.day.text = "${dayName(System.currentTimeMillis())}"
                     binding.date.text = date()
                     binding.cityName.text = "$cityname"
                     binding.weather.text = "$condition"

                     changeImagesAccordingWeather(condition)
                     val con = responseBody.weather.firstOrNull()?.main ?: "Unknown"
                 }

             }

             override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                 Log.e("TAG", "API call failed: ${t.message}")
                 Toast.makeText(this@MainActivity, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
             }

         })
    }

    private fun changeImagesAccordingWeather(conditions : String) {
        when(conditions){
            "Haze","Partly Clouds","Clouds","Overcast","Mist","Foggy","Fog","Smoke"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(currentTimeMillis: Long): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(Date(currentTimeMillis*1000))
    }

    private fun dayName(currentTimeMillis: Long): String {
        val sdf = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault())
        return sdf.format(Date())
    }


}