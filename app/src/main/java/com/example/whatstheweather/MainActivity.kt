package com.example.whatstheweather

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var city : EditText
    private lateinit var weather : TextView

    fun clear(view : View){
        city.text.clear()
    }

    fun findWeather(view : View){
        val cityName : String = city.text.toString()

        val task : WeatherDownload = @SuppressLint("StaticFieldLeak")
        object : WeatherDownload() {}

        try {
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=b30a0fa427a7b3cdbd37ab4a0cce7039&units=metric").get()
        }catch (e : Exception){
            e.printStackTrace()
        }

        val mgr : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(city.windowToken, 0)
    }

    open inner class WeatherDownload : AsyncTask<String, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg urls: String?): String? {

            try {
                var result = ""
                val url = URL(urls[0])
                val urlConnection: HttpURLConnection = (url.openConnection() as HttpURLConnection?)!!
                val input: InputStream? = urlConnection.inputStream
                val reader: InputStreamReader = object : InputStreamReader(input) {}
                var data: Int = reader.read()

                while (data != -1) {
                    val current: Char = data.toChar()
                    result += current
                    data = reader.read()
                }

                return result
            } catch (e : Exception){
                Log.i("Exception", e.printStackTrace().toString())

                runOnUiThread {
                    Toast.makeText(applicationContext, "Could not find weather :(", Toast.LENGTH_SHORT).show()
                }
                return null
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                val jsonObject: JSONObject = object : JSONObject(result.toString()) {}

                val weatherData: String = jsonObject.getString("weather")
                val temp: JSONObject = jsonObject.getJSONObject("main")
                val wind: JSONObject = jsonObject.getJSONObject("wind")

                Log.i("Weather Content", weatherData)

                val arr: JSONArray = object : JSONArray(weatherData) {}
                var message = ""

                for (i in 0..<arr.length()) {
                    val jsonPart: JSONObject = arr.getJSONObject(i)

                    val main : String = jsonPart.getString("main")
                    val description : String = jsonPart.getString("description")

                    if(main != "" && description != ""){
                        message += "$main: $description\n"
                    }
                }

                val temperature: String = temp.getString("temp")
                val humidity : String = temp.getString("humidity")

                if(temperature != "" && humidity != "") {
                    message += "Temp: $temperature \u2103 \n"
                    message += "Humidity: $humidity %\n"
                }

                val speed: String = wind.getString("speed")

                if(speed != ""){
                    message += "Wind Speed: $speed m/s\n"
                }

                if(message != ""){
                    weather.text = message
                }
                else{
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Could not find weather :(", Toast.LENGTH_SHORT).show()
                    }

                }
            }catch (e : Exception){
                Log.i("Exception", e.printStackTrace().toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        city = findViewById(R.id.city)
        weather = findViewById(R.id.Weather)

        var name = "Kanak"
    }
}