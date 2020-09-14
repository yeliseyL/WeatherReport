package elisey.lobanov.weatherreport;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import elisey.lobanov.weatherreport.Connection.OnlineConnection;
import elisey.lobanov.weatherreport.Connection.WeatherRequest;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

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

        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final WeatherRequest weatherRequest = new OnlineConnection(getContext()).getData(cityNameText);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setValues(weatherRequest);
                    }
                });
            }
        }).start();
    }

    private void setValues(WeatherRequest weatherRequest) {

        if (weatherRequest != null) {
            cityName.setText(weatherRequest.getName());
            mainTemp.setText(String.format(getResources().getString(R.string.main_temp_string),
                    (int) weatherRequest.getMain().getTemp(), getResources().getString(R.string.degree_sign)));
            description.setText(capitalize(weatherRequest.getWeather()[0].getDescription()));

            if (isWindSpeedTextView) {
                windSpeedTextView.setVisibility(View.VISIBLE);
                windSpeedTextView.setText(String.format(getResources().getString(R.string.wind_string),
                        (int) weatherRequest.getWind().getSpeed()));
            } else {
                windSpeedTextView.setVisibility(View.GONE);
            }

            if (isAtmPressureTextView) {
                atmPressureTextView.setVisibility(View.VISIBLE);
                atmPressureTextView.setText(String.format(getResources().getString(R.string.pressure_string),
                        (int)((float) weatherRequest.getMain().getPressure() * 0.75)));
            } else {
                atmPressureTextView.setVisibility(View.GONE);
            }

            HistoryHandler historyHandler = HistoryHandler.getInstance();
            historyHandler.setHistoryEntry(cityName.getText().toString(), mainTemp.getText().toString());
        }
    }

    public static String capitalize(String str) {
        if (str == null) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}