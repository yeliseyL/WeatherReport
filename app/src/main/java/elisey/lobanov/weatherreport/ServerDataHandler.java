package elisey.lobanov.weatherreport;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

import elisey.lobanov.weatherreport.Constants;
import elisey.lobanov.weatherreport.connection.WeatherRequest;

public class ServerDataHandler extends IntentService implements Constants {

    public ServerDataHandler() {
        super("ServerDataHandler");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String weatherJSON = intent.getStringExtra(WR_DATA);
        Gson gson = new Gson();
        WeatherRequest wr = gson.fromJson(weatherJSON, WeatherRequest.class);

        String cityName = wr.getName();
        int mainTemp = (int) wr.getMain().getTemp();
        String description = wr.getWeather()[0].getDescription();
        int windSpeed = (int) wr.getWind().getSpeed();
        int pressure = (int)((float) wr.getMain().getPressure() * 0.75);

        Intent broadcastIntent = new Intent(DATA_RESULT);
        broadcastIntent.putExtra(NAME, cityName);
        broadcastIntent.putExtra(TEMP, mainTemp);
        broadcastIntent.putExtra(DESCRIPTION, description);
        broadcastIntent.putExtra(WIND, windSpeed);
        broadcastIntent.putExtra(PRESSURE, pressure);

        sendBroadcast(broadcastIntent);
    }

}
