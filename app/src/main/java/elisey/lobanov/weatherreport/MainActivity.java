package elisey.lobanov.weatherreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Constants{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Weather Report");

        final Button citySelectBtn = findViewById(R.id.button4);
        final Button infoBtn = findViewById(R.id.infoBtn);
        final TextView windSpeedTextView = findViewById(R.id.windSpeedTextView);
        final TextView atmPressureTextView = findViewById(R.id.atmPressureTextView);
        final TextView cityName = findViewById(R.id.textView);

        citySelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityChooserParcel cityChooserParcel = new CityChooserParcel();
                cityChooserParcel.cityName = cityName.getText().toString();
                cityChooserParcel.isWindSpeedVisible = windSpeedTextView.getVisibility() == View.VISIBLE;
                cityChooserParcel.isPressureVisible = atmPressureTextView.getVisibility() == View.VISIBLE;
                Intent intent = new Intent(MainActivity.this, CitySelectionActivity.class);
                intent.putExtra(FIELDS, cityChooserParcel);
                startActivity(intent);
            }
        });

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView cityName = findViewById(R.id.textView);
                String url = String.format("https://wikipedia.org/wiki/%s", cityName.getText());
                Uri uri = Uri.parse(url);
                Intent openSite = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(openSite);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final TextView windSpeedTextView = findViewById(R.id.windSpeedTextView);
        final TextView atmPressureTextView = findViewById(R.id.atmPressureTextView);
        final TextView cityName = findViewById(R.id.textView);

        try {
            CityChooserParcel cityChooserParcel = (CityChooserParcel) getIntent().getExtras().getSerializable(FIELDS);
            cityName.setText(cityChooserParcel.cityName);

            if (cityChooserParcel.isWindSpeedVisible) {
                windSpeedTextView.setVisibility(View.VISIBLE);
            } else {
                windSpeedTextView.setVisibility(View.GONE);
            }

            if (cityChooserParcel.isPressureVisible) {
                atmPressureTextView.setVisibility(View.VISIBLE);
            } else {
                atmPressureTextView.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            return;
        }
    }
}
