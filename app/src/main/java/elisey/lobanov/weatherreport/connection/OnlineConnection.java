package elisey.lobanov.weatherreport.connection;

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

public class OnlineConnection extends IntentService implements Constants {

    public OnlineConnection() {
        super("OnlineConnection");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String cityName = intent.getStringExtra(CITY_NAME);
        String wr = getData(cityName);
        Intent broadcastIntent = new Intent(BROADCAST_RESULT);
        broadcastIntent.putExtra(WR_RESULT, wr);
        sendBroadcast(broadcastIntent);
    }

    public String getData(String cityName) {
        String wr = null;

        try {
            final URL uri = new URL(makeUrl(cityName));
            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) uri.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                wr = getLines(in);

//                Gson gson = new Gson();
//                wr = gson.fromJson(result, WeatherRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != urlConnection) {
                    urlConnection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return wr;
    }

    private String makeUrl(String cityName) {
        final String API_KEY = "bde7c62807efaf4028c2ddfd0c3d1bfe";
        return String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s", cityName, API_KEY);
    }

    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }


}
