package com.example.prakritikdrishti;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;

import com.example.prakritikdrishti.databinding.ActivityMainBinding;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchWeatherData("chandigarh");
        SearchCity();
    }

    private void SearchCity() {
        SearchView searchView = binding.searchView; // Assuming binding is our view binding object
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    fetchWeatherData(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // i have to update toast message here
              //  Toast.makeText(MainActivity.this, "Searching For " + newText, Toast.LENGTH_LONG).show();

                return false;
            }
        });
    }


    private void fetchWeatherData(String cityName) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<WeatherApp> call = apiInterface.getWeatherData(cityName, "256b8b749f41276c55755680a12e3235", "metric");
        call.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
                WeatherApp responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    String temperature = String.valueOf(responseBody.getMain().getTemp());
                    double humidity = responseBody.getMain().getHumidity();
                    double windSpeed = responseBody.getWind().getSpeed();
                    long sunrise = responseBody.getSys().getSunrise();
                    long sunset = responseBody.getSys().getSunset();
                    double seaLevel = responseBody.getMain().getPressure();
                    String condition = responseBody.getWeather().size() > 0 ? responseBody.getWeather().get(0).getMain() : "unknown";
                    double temp_max= responseBody.getMain().getTempMax();
                    double temp_min = responseBody.getMain().getTempMin();
                    String name =  responseBody.getName();
                    binding.temp.setText(temperature + " °C");
                    binding.weather.setText(condition);
                    binding.maxtemp.setText("Max Temp: " + temp_max + "°C");
                    binding.mintemp.setText("Min Temp: " + temp_min + " °C");
                    binding.humidity.setText(humidity + "%");
                    binding.windSpeed.setText(windSpeed + " m/s");
                    binding.sunrise.setText(time(sunrise));
                    binding.sunset.setText(time(sunset));

                    binding.sea.setText(String.valueOf(seaLevel));
                    binding.condition.setText(condition);
                    binding.cityname.setText(name);
                    binding.day.setText(dayName(System.currentTimeMillis()));
                    binding.date.setText(date());
                    changeImagesAccordingToWeatherCondition(condition);
                    // Log the temperature using a logging library of our choice
                   // Log.d("WeatherApp", "Temperature: " + temperature);
                }

            }

            @Override
            public void onFailure(Call<WeatherApp> call, Throwable throwable) {
               // Log.e("WeatherApp", "Error fetching weather data", throwable);

                // Check for network connection error
                if (throwable instanceof IOException) {
                    Toast.makeText(MainActivity.this, "Network Connection error", Toast.LENGTH_LONG).show();
                }
            }

        });


    }


    private void changeImagesAccordingToWeatherCondition(String conditions) {
        switch (conditions) {
            case "Clear Sky":
            case "Sunny":
            case "Clear":
            case "Partly Clouds":
                binding.getRoot().setBackgroundResource(R.drawable.sunnybg);
                binding.lottieAnimationView.setAnimation(R.raw.sunanimation);
                binding.cityname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location32, 0, 0, 0);
                setTextColorBlack();
                break;

            case "Haze":
            case "Clouds":
            case "Overcast":
            case "Mist":
            case "Foggy":
                binding.getRoot().setBackgroundResource(R.drawable.cloudybg);
                binding.cityname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.locationwhite, 0, 0, 0);
                binding.lottieAnimationView.setAnimation(R.raw.cloudanimation);
                setTextColorWhite();
                break;
            case "Rain":
            case "Light Rain":
            case "Drizzle":
            case "Moderate Rain":
            case "Showers":
            case "Heavy Rain":
                binding.getRoot().setBackgroundResource(R.drawable.rainybg);
                binding.lottieAnimationView.setAnimation(R.raw.rainanimation);
                binding.cityname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.locationwhite, 0, 0, 0);
                setTextColorWhite();
                break;

            case "Light Snow":
            case "Moderate Snow":
            case "Heavy Snow":
            case "Blizzard":
                binding.getRoot().setBackgroundResource(R.drawable.snowbg);
                binding.lottieAnimationView.setAnimation(R.raw.snowanimation);
                binding.cityname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location32, 0, 0, 0);
                setTextColorBlack();
                break;

            default:
                binding.getRoot().setBackgroundResource(R.drawable.sunnybg);
                binding.lottieAnimationView.setAnimation(R.raw.sunanimation);
                binding.cityname.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location32, 0, 0, 0);
                setTextColorBlack();
                break;
        }
    }

    private void setTextColorBlack() {
        binding.temp.setTextColor(Color.BLACK);
        binding.weather.setTextColor(Color.BLACK);
        binding.maxtemp.setTextColor(Color.BLACK);
        binding.mintemp.setTextColor(Color.BLACK);
        binding.textView5.setTextColor(Color.BLACK);
        binding.cityname.setTextColor(Color.BLACK);
        binding.day.setTextColor(Color.BLACK);
        binding.date.setTextColor(Color.BLACK);

    }

    private void setTextColorWhite() {
        binding.temp.setTextColor(Color.WHITE);
        binding.weather.setTextColor(Color.WHITE);
        binding.maxtemp.setTextColor(Color.WHITE);
        binding.mintemp.setTextColor(Color.WHITE);
        binding.textView5.setTextColor(Color.WHITE);
        binding.cityname.setTextColor(Color.WHITE);
        binding.day.setTextColor(Color.WHITE);
        binding.date.setTextColor(Color.WHITE);
    }


    private String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    private String time(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp*1000));
    }

    public static String dayName(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}