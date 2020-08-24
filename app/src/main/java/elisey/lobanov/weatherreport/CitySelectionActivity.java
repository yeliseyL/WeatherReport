package elisey.lobanov.weatherreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class CitySelectionActivity extends AppCompatActivity implements Constants{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        Button applyBtn = findViewById(R.id.chooseCityApplyBtn);
        final CheckBox showWindSpeedCheckbox = findViewById(R.id.checkBox);
        final CheckBox showAtmPressureCheckbox = findViewById(R.id.checkBox2);
        final EditText cityNameEditText = findViewById(R.id.cityNameText);
        final MainPresenter presenter = MainPresenter.getInstance();

        CityChooserParcel cityChooserParcel = (CityChooserParcel) getIntent().getExtras().getSerializable(FIELDS);
        cityNameEditText.setText(cityChooserParcel.cityName);
        showWindSpeedCheckbox.setChecked(cityChooserParcel.isWindSpeedVisible);
        showAtmPressureCheckbox.setChecked(cityChooserParcel.isPressureVisible);

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityChooserParcel cityChooserParcel = new CityChooserParcel();
                cityChooserParcel.cityName = cityNameEditText.getText().toString();
                cityChooserParcel.isWindSpeedVisible = showWindSpeedCheckbox.isChecked();
                cityChooserParcel.isPressureVisible = showAtmPressureCheckbox.isChecked();
                Intent intent = new Intent(CitySelectionActivity.this, MainActivity.class);
                intent.putExtra(FIELDS, cityChooserParcel);
                startActivity(intent);
                finish();
            }
        });
    }
}