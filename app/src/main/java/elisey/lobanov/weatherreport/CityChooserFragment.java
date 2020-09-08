package elisey.lobanov.weatherreport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.regex.Pattern;

public class CityChooserFragment extends Fragment implements Constants, FragmentCallback {

    private CityChooserParcel cityChooserParcel;
    private FragmentCallback fragmentCallback;
    private CheckBox showWindSpeedCheckbox;
    private CheckBox showAtmPressureCheckbox;
    private TextInputEditText cityNameEditText;
    private String[] cities;

    public static CityChooserFragment create(CityChooserParcel parcel) {
        CityChooserFragment fragment = new CityChooserFragment();
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
        return  inflater.inflate(R.layout.fragment_city_chooser_coordinator, container, false);
    }

    public void setFragmentCallback(FragmentCallback callback) {
        this.fragmentCallback = callback;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showWindSpeedCheckbox = view.findViewById(R.id.checkBox);
        showAtmPressureCheckbox = view.findViewById(R.id.checkBox2);
        cityNameEditText = view.findViewById(R.id.cityNameText);

        cities = getResources().getStringArray(R.array.cities_array);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCity);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapterCity adapter = new RecyclerViewAdapterCity(cities);
        adapter.setFragmentCallback(this);
        recyclerView.setAdapter(adapter);

        if (getArguments().getSerializable(FIELDS) != null) {
            cityChooserParcel = (CityChooserParcel) getArguments().getSerializable(FIELDS);
        }

        showWindSpeedCheckbox.setChecked(cityChooserParcel.isWindSpeedVisible());
        showAtmPressureCheckbox.setChecked(cityChooserParcel.isPressureVisible());

        FloatingActionButton applyBtn = view.findViewById(R.id.chooseCityApplyBtn);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cityNameEditText.getText().toString().equals("")) {
                    if (isValid(cityNameEditText)) {
                        Snackbar.make(v, "Apply changes?", Snackbar.LENGTH_LONG)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String cityName = cityNameEditText.getText().toString().replaceAll("\\s", "+");
                                        cityChooserParcel.setCityName(cityName);
                                        cityChooserParcel.setWindSpeedVisible(showWindSpeedCheckbox.isChecked());
                                        cityChooserParcel.setPressureVisible(showAtmPressureCheckbox.isChecked());

                                        if(fragmentCallback != null){
                                            fragmentCallback.refreshInfo(cityChooserParcel);
                                        }

                                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        }
                                    }
                                }).show();
                    } else {
                        return;
                    }
                }
            }
        });
    }

    private boolean isValid(TextView view){
        String message = "Incorrect city name";
        String value = view.getText().toString();
        Pattern checkCityName = Pattern.compile("^[A-Za-z ]{2,}$");
        if (checkCityName.matcher(value).matches()){
            view.setError(null);
            return true;
        }
        else{
            view.setError(message);
            return false;
        }
    }

    @Override
    public void refreshInfo(CityChooserParcel parcel) {
        cityNameEditText.setText(cityChooserParcel.getCityName());
    }
}