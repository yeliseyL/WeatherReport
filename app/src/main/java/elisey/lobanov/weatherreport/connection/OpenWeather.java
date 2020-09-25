package elisey.lobanov.weatherreport.connection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query(value = "q", encoded = true) String cityName, @Query("units") String units, @Query("appid") String keyApi);
}
