package com.example.benjiweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar idPBLoading;
    private RelativeLayout idView;
    private ImageView black;
    private TextView CityLocation;
    private TextInputLayout idcity;
    private TextInputEditText idCityy;
    private ImageView Search;
    private TextView Temperature;
    private ImageView TempImage;
    private TextView WeatherNext;
    private TextView weatherForecast;
    private RecyclerView Weather;
    private ArrayList<WeatherBuildUp> weatherBuildUpArrayList;
    private Adapter adapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String CityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        idPBLoading = findViewById(R.id.idPBLoading);
        idView = findViewById(R.id.View);
        black = findViewById(R.id.black);
        CityLocation = findViewById(R.id.CityLocation);
        idcity = findViewById(R.id.idcity);
        idCityy = findViewById(R.id.idCityy);
        Search = findViewById(R.id.Search);
        Temperature = findViewById(R.id.Temperature);
        TempImage = findViewById(R.id.TempImage);
        WeatherNext = findViewById(R.id.WeatherNext);
        weatherForecast = findViewById(R.id.weatherForecast);
        Weather = findViewById(R.id.Weather);
        weatherBuildUpArrayList = new ArrayList<>();
        adapter = new Adapter(this, weatherBuildUpArrayList);
        Weather.setAdapter(adapter);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            CityName = getCity(location.getLongitude(), location.getLatitude());
            getWeatherInfo(CityName);
        } else {
            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
        }

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = idCityy.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                } else {
                    CityLocation.setText(city);
                    getWeatherInfo(city);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please Provide The Permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCity(double lon, double lat) {
        String city = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lat, lon, 10);

            for (Address adr : addresses) {
                if (adr != null) {
                    String locality = adr.getLocality();
                    if (locality != null && !locality.equals("")) {
                        city = locality;
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "User's City Not Found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }

    private void getWeatherInfo(String city) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=cde214c168a342ba823221008242006&q=" + city + "&days=1&aqi=no&alerts=no";
        CityLocation.setText(city);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                idPBLoading.setVisibility(View.GONE);
                idView.setVisibility(View.VISIBLE);
                weatherBuildUpArrayList.clear();

                try {
                    JSONObject current = response.getJSONObject("current");
                    String temperature = current.getString("temp_c");
                    Temperature.setText(temperature + "°C");

                    int isDay = current.getInt("is_day");
                    JSONObject condition = current.getJSONObject("condition");
                    String conditionText = condition.getString("text");
                    String conditionIcon = condition.getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(TempImage);
                    WeatherNext.setText(conditionText);

                    if (isDay == 1) {
                        // morning
                        Picasso.get().load("https://w0.peakpx.com/wallpaper/461/689/HD-wallpaper-purple-stars-gradient-space-star-thumbnail.jpg").into(black);
                    } else {
                        // night
                        Picasso.get().load("https://w0.peakpx.com/wallpaper/832/271/HD-wallpaper-blue-stars-gradient-space-star-thumbnail.jpg").into(black);
                    }

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONArray forecastDayArray = forecast.getJSONArray("forecastday");
                    JSONObject forecastDay = forecastDayArray.getJSONObject(0);
                    JSONArray hourArray = forecastDay.getJSONArray("hour");

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourOBJ = hourArray.getJSONObject(i);
                        String time = hourOBJ.getString("time");
                        String tempC = hourOBJ.getString("temp_c");
                        String imgIcon = hourOBJ.getJSONObject("condition").getString("icon");
                        String windKph = hourOBJ.getString("wind_kph");

                        weatherBuildUpArrayList.add(new WeatherBuildUp(time, tempC, imgIcon, windKph));
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                idPBLoading.setVisibility(View.GONE);
                Log.e("VolleyError", "Error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                    Log.e("VolleyError", "Response Data: " + new String(error.networkResponse.data));
                }
                Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }}
