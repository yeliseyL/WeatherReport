package elisey.lobanov.weatherreport;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Objects;

public class CityChooserFragment extends Fragment implements Constants {

    private CityChooserParcel cityChooserParcel;
    private FragmentCallback fragmentCallback;
    private CheckBox showWindSpeedCheckbox;
    private CheckBox showAtmPressureCheckbox;
    private EditText cityNameEditText;

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
        return  inflater.inflate(R.layout.fragment_city_chooser, container, false);
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
        recyclerView.setAdapter(adapter);

        if (getArguments().getSerializable(FIELDS) != null) {
            cityChooserParcel = (CityChooserParcel) getArguments().getSerializable(FIELDS);
        }
        if (cityChooserParcel.getCityName() != null) {
            cityNameEditText.setText("");
        }
        showWindSpeedCheckbox.setChecked(cityChooserParcel.isWindSpeedVisible());
        showAtmPressureCheckbox.setChecked(cityChooserParcel.isPressureVisible());

        Button applyBtn = view.findViewById(R.id.chooseCityApplyBtn);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cityNameEditText.getText().toString().equals("")) {
                    cityChooserParcel.setCityName(cityNameEditText.getText().toString());
                }
                cityChooserParcel.setWindSpeedVisible(showWindSpeedCheckbox.isChecked());
                cityChooserParcel.setPressureVisible(showAtmPressureCheckbox.isChecked());

                if(fragmentCallback != null){
                    fragmentCallback.refreshInfo(cityChooserParcel);
                }

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
}