package elisey.lobanov.weatherreport;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class MainFragment extends Fragment implements Constants, FragmentCallback {

    CityChooserParcel parcel;
    private TextView cityName;
    private TextView windSpeedTextView;
    private TextView atmPressureTextView;
    boolean isLandscapeOrientation;

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

        final Button citySelectBtn = view.findViewById(R.id.button4);
        final Button infoBtn = view.findViewById(R.id.infoBtn);
        cityName = view.findViewById(R.id.textView);
        windSpeedTextView = view.findViewById(R.id.windSpeedTextView);
        atmPressureTextView = view.findViewById(R.id.atmPressureTextView);

        refreshInfo(parcel);

        final Fragment fragment = CityChooserFragment.create(parcel);
        ((CityChooserFragment) fragment).setFragmentCallback(this);

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
        this.cityName.setText(parcel.getCityName());

        if (parcel.isWindSpeedVisible()) {
            windSpeedTextView.setVisibility(View.VISIBLE);
        } else {
            windSpeedTextView.setVisibility(View.GONE);
        }

        if (parcel.isPressureVisible()) {
            atmPressureTextView.setVisibility(View.VISIBLE);
        } else {
            atmPressureTextView.setVisibility(View.GONE);
        }
    }
}