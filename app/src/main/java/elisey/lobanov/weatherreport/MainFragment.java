package elisey.lobanov.weatherreport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;

import elisey.lobanov.weatherreport.connection.OnlineConnection;
import elisey.lobanov.weatherreport.connection.OpenWeather;
import elisey.lobanov.weatherreport.connection.WeatherRequest;
import elisey.lobanov.weatherreport.history.HistoryDao;
import elisey.lobanov.weatherreport.history.HistoryField;
import elisey.lobanov.weatherreport.history.HistorySource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements Constants, FragmentCallback {

    static final String url = "https://api.openweathermap.org/";
    static final String keyApi = "bde7c62807efaf4028c2ddfd0c3d1bfe";
    static final String units = "metric";
    private SharedPreferences sharedPref;
    private CityChooserParcel parcel;
    private TextView cityName;
    private TextView mainTemp;
    private TextView description;
    private TextView windSpeedTextView;
    private TextView atmPressureTextView;
    TextView token;
    private String cityNameText;
    private boolean isWindSpeedTextView;
    private boolean isAtmPressureTextView;
    boolean isLandscapeOrientation;
    private OpenWeather openWeather;
    private HistorySource historySource;
    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";
    private static final int PERMISSION_REQUEST_CODE = 10;

    private GoogleSignInClient googleSignInClient;
    private com.google.android.gms.common.SignInButton buttonSignIn;
    private MaterialButton buttonSignOut;

    private String[] times;
    private String[] timeTemps;
    private int messageId = 0;

    public static MainFragment create(CityChooserParcel parcel) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(FIELDS, parcel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getActivity().getPreferences(MODE_PRIVATE);
        initRetorfit();
        requestPermissions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        ImageView imageView = view.findViewById(R.id.imageView3);
        Picasso.get()
//                .load("https://images.unsplash.com/photo-1499956827185-0d63ee78a910")
                .load(R.drawable.cloudy_sky_bg)
                .into(imageView);

        parcel = (CityChooserParcel) getArguments().getSerializable(FIELDS);

        times = getResources().getStringArray(R.array.time_array);
        timeTemps = getResources().getStringArray(R.array.temp_array);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapterMain adapter = new RecyclerViewAdapterMain(times, timeTemps);
        recyclerView.setAdapter(adapter);

        final Button citySelectBtn = view.findViewById(R.id.button4);
        final Button infoBtn = view.findViewById(R.id.infoBtn);
        final Button getLocalWeatherBtn = view.findViewById(R.id.getLocalWeather);
        cityName = view.findViewById(R.id.textView);
        mainTemp = view.findViewById(R.id.textView2);
        description = view.findViewById(R.id.textView3);
        windSpeedTextView = view.findViewById(R.id.windSpeedTextView);
        atmPressureTextView = view.findViewById(R.id.atmPressureTextView);

        buttonSignIn = view.findViewById(R.id.sign_in_button);
        buttonSignOut = view.findViewById(R.id.sing_out_button);
        token = view.findViewById(R.id.token);

        googleAuthInit();
        enableSign();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            disableSign();
            updateUI(account.getEmail());
        }

        loadPreferences(sharedPref);
        requestRetrofit(cityNameText);

        getLocalWeatherBtn.setOnClickListener(v -> {
            getAndSaveCoordinates();
            String latitude = sharedPref.getString(LATITUDE, "0");
            String longitude = sharedPref.getString(LONGITUDE, "0");
            requestRetrofit(latitude, longitude);
        });

        infoBtn.setOnClickListener(v -> {
            final TextView cityName = view.findViewById(R.id.textView);
            String url = String.format("https://en.wikipedia.org/wiki/%s", cityName.getText());
            Uri uri = Uri.parse(url);
            Intent openSite = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(openSite);
        });
        return view;
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getAndSaveCoordinates();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void getAndSaveCoordinates() {

        SharedPreferences.Editor editor = sharedPref.edit();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(provider, 100000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String latitude = Double.toString(location.getLatitude());
                String longitude = Double.toString(location.getLongitude());
                editor.putString(LATITUDE, latitude);
                editor.putString(LONGITUDE, longitude);
                editor.apply();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                getAndSaveCoordinates();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener((Executor) this, task -> {
                    updateUI("email");
                    enableSign();
                });
    }

    private void googleAuthInit() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        buttonSignIn.setOnClickListener(v -> signIn()
        );
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            disableSign();
            updateUI(account.getEmail());
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void updateUI(String idToken) {
        token.setText(idToken);
    }

    private void enableSign(){
        buttonSignIn.setEnabled(true);
        buttonSignOut.setEnabled(false);
    }

    private void disableSign(){
        buttonSignIn.setEnabled(false);
        buttonSignOut.setEnabled(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isLandscapeOrientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void refreshInfo(CityChooserParcel parcel) {
        cityNameText = parcel.getCityName();
        isWindSpeedTextView = parcel.isWindSpeedVisible();
        isAtmPressureTextView = parcel.isPressureVisible();

        requestRetrofit(cityNameText);
    }

    private void initRetorfit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String cityName) {
        openWeather.loadWeather(cityName, units, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            String parsedCityName = response.body().getName();
                            int parsedMainTemp = (int) response.body().getMain().getTemp();
                            String parsedDescription = response.body().getWeather()[0].getDescription();
                            int parsedWindSpeed = (int) response.body().getWind().getSpeed();
                            if (parsedWindSpeed > 15) {
                                stormWarning(parsedWindSpeed);
                            }
                            int parsedPressure = (int) ((float) response.body().getMain().getPressure() * 0.75);
                            setValues(parsedCityName, parsedMainTemp, parsedDescription, parsedWindSpeed, parsedPressure);
                            savePreferences(sharedPref);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        BottomSheetErrorDialog.newInstance();
                    }
                });
    }

    private void stormWarning(int windSpeed) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.weather_warning))
                .setContentText("Attention! Wind speed is " + windSpeed);
        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }

    private void requestRetrofit(String latitude, String longitude) {
        openWeather.loadWeather(latitude, longitude, units, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            String parsedCityName = response.body().getName();
                            int parsedMainTemp = (int) response.body().getMain().getTemp();
                            String parsedDescription = response.body().getWeather()[0].getDescription();
                            int parsedWindSpeed = (int) response.body().getWind().getSpeed();
                            int parsedPressure = (int) ((float) response.body().getMain().getPressure() * 0.75);
                            setValues(parsedCityName, parsedMainTemp, parsedDescription, parsedWindSpeed, parsedPressure);
                            savePreferences(sharedPref);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        BottomSheetErrorDialog.newInstance();
                    }
                });
    }

    @SuppressLint("SimpleDateFormat")
    private void setValues(String parsedCityName, int parsedMainTemp, String parsedDescription, int parsedWindSpeed, int parsedPressure) {

        cityName.setText(parsedCityName);
        mainTemp.setText(String.format(getResources().getString(R.string.main_temp_string),
                parsedMainTemp, getResources().getString(R.string.degree_sign)));
        description.setText(capitalize(parsedDescription));

        if (isWindSpeedTextView) {
            windSpeedTextView.setVisibility(View.VISIBLE);
            windSpeedTextView.setText(String.format(getResources().getString(R.string.wind_string),
                    parsedWindSpeed));
        } else {
            windSpeedTextView.setVisibility(View.GONE);
        }

        if (isAtmPressureTextView) {
            atmPressureTextView.setVisibility(View.VISIBLE);
            atmPressureTextView.setText(String.format(getResources().getString(R.string.pressure_string),
                    parsedPressure));
        } else {
            atmPressureTextView.setVisibility(View.GONE);
        }
        parcel.setCityName(parsedCityName);

        HistoryDao historyDao = App
                .getInstance()
                .getHistoryDao();
        historySource = new HistorySource(historyDao);
        HistoryField historyField = new HistoryField();
        historyField.date = getDate();
        historyField.cityName = cityName.getText().toString();
        historyField.temp = mainTemp.getText().toString();
        historySource.addHistoryField(historyField);

    }

    private void savePreferences(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CITY_NAME, cityName.getText().toString());
        editor.putString(TEMP, mainTemp.getText().toString());
        editor.putString(DESCRIPTION, description.getText().toString());
        editor.putString(WIND, windSpeedTextView.getText().toString());
        editor.putString(PRESSURE, atmPressureTextView.getText().toString());
        editor.putBoolean(WIND_VISIBLE, isWindSpeedTextView);
        editor.putBoolean(PRESSURE_VISIBLE, isAtmPressureTextView);
        editor.apply();
    }

    private void loadPreferences(SharedPreferences sharedPref) {
        cityName.setText(sharedPref.getString(CITY_NAME, ""));
        mainTemp.setText(sharedPref.getString(TEMP, "0"));
        description.setText(sharedPref.getString(DESCRIPTION, ""));

        if (sharedPref.getBoolean(WIND_VISIBLE, false)) {
            windSpeedTextView.setVisibility(View.VISIBLE);
        } else {
            windSpeedTextView.setVisibility(View.GONE);
        }

        if (sharedPref.getBoolean(PRESSURE_VISIBLE, false)) {
            atmPressureTextView.setVisibility(View.VISIBLE);
        } else {
            atmPressureTextView.setVisibility(View.GONE);
        }

        windSpeedTextView.setText(sharedPref.getString(WIND, "0"));
        atmPressureTextView.setText(sharedPref.getString(PRESSURE, "0"));
    }

    public static String capitalize(String str) {
        if (str == null) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(calendar.getTime());
    }
}