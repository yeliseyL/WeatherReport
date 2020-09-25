package elisey.lobanov.weatherreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import elisey.lobanov.weatherreport.connection.OnlineConnection;
import elisey.lobanov.weatherreport.connection.OpenWeather;
import elisey.lobanov.weatherreport.connection.WeatherRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment implements Constants, FragmentCallback {

    CityChooserParcel parcel;
    private TextView cityName;
    private TextView mainTemp;
    private TextView description;
    private TextView windSpeedTextView;
    private TextView atmPressureTextView;
    private String cityNameText;
    private boolean isWindSpeedTextView;
    private boolean isAtmPressureTextView;
    boolean isLandscapeOrientation;
    String weatherJSON;
    private OpenWeather openWeather;

    private String[] times;
    private String[] timeTemps;

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
        initRetorfit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        ImageView imageView = view.findViewById(R.id.imageView3);
        Picasso.get()
                .load("https://images.unsplash.com/photo-1499956827185-0d63ee78a910")
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
        cityName = view.findViewById(R.id.textView);
        mainTemp = view.findViewById(R.id.textView2);
        description = view.findViewById(R.id.textView3);
        windSpeedTextView = view.findViewById(R.id.windSpeedTextView);
        atmPressureTextView = view.findViewById(R.id.atmPressureTextView);

        refreshInfo(parcel);

        final Fragment fragment = CityChooserFragment.create(parcel);
        ((CityChooserFragment) fragment).setFragmentCallback(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            citySelectBtn.setVisibility(View.GONE);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.city_chooser_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            citySelectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    if (!isLandscapeOrientation) {
                        ft.add(R.id.main_container, fragment);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                }
            });
        }

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView cityName = view.findViewById(R.id.textView);
                String url = String.format("https://en.wikipedia.org/wiki/%s", cityName.getText());
                Uri uri = Uri.parse(url);
                Intent openSite = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(openSite);
            }
        });
        return view;
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
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String cityName) {
        String keyApi = "bde7c62807efaf4028c2ddfd0c3d1bfe";
        String units = "metric";
        openWeather.loadWeather(cityName, units, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            String parsedCityName = response.body().getName();
                            int parsedMainTemp = (int) response.body().getMain().getTemp();
                            String parsedDescription = response.body().getWeather()[0].getDescription();
                            int parsedWindSpeed = (int) response.body().getWind().getSpeed();
                            int parsedPressure = (int)((float) response.body().getMain().getPressure() * 0.75);
                            setValues(parsedCityName, parsedMainTemp, parsedDescription, parsedWindSpeed, parsedPressure);
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        BottomSheetErrorDialog.newInstance();
                    }
                });
    }

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

        HistoryHandler historyHandler = HistoryHandler.getInstance();
        historyHandler.setHistoryEntry(cityName.getText().toString(), mainTemp.getText().toString());
    }

    public static String capitalize(String str) {
        if (str == null) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}