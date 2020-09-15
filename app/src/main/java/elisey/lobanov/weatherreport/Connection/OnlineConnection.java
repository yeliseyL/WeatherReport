package elisey.lobanov.weatherreport.Connection;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

import elisey.lobanov.weatherreport.BottomSheetErrorDialog;

public class OnlineConnection {
    private Context context;

    public OnlineConnection(Context context) {
        this.context = context;
    }

    public WeatherRequest getData(String cityName) {
        WeatherRequest wr = null;

        try {
            final URL uri = new URL(makeUrl(cityName));
            HttpsURLConnection urlConnection = null;
            try {
                urlConnection = (HttpsURLConnection) uri.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String result = getLines(in);

                Gson gson = new Gson();
                wr = gson.fromJson(result, WeatherRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != urlConnection) {
                    urlConnection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            Toast.makeText(context, "Wrong URL address", Toast.LENGTH_LONG).show();
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
