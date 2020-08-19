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

public class CitySelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        Button applyBtn = findViewById(R.id.chooseCityApplyBtn);
        final MainPresenter presenter = MainPresenter.getInstance();

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox showWindSpeedCheckbox = findViewById(R.id.checkBox);
                CheckBox showAtmPressureCheckbox = findViewById(R.id.checkBox2);
                EditText cityNameEditText = findViewById(R.id.cityNameText);
                presenter.setShowWindSpeed(showWindSpeedCheckbox.isChecked());
                presenter.setShowAtmPressure(showAtmPressureCheckbox.isChecked());
                presenter.setCityName(cityNameEditText.getText().toString());
                finish();
            }
        });

        String instanceState;
        if (savedInstanceState == null){
            instanceState = "Первый запуск!";
        }
        else{
            instanceState = "Повторный запуск!";
        }
        Toast.makeText(getApplicationContext(), instanceState + " - onCreate()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", instanceState + " - onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "CitySelectionActivity onStart()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "onStart()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState){
        super.onRestoreInstanceState(saveInstanceState);
        Toast.makeText(getApplicationContext(), "Повторный запуск!! - onRestoreInstanceState()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "Повторный запуск!! - onRestoreInstanceState()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "CitySelectionActivity onStop()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "CitySelectionActivity onDestroy()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "CitySelectionActivity onPause()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "CitySelectionActivity onResume()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "onResume()");
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        Toast.makeText(getApplicationContext(), "CitySelectionActivity onSaveInstanceState()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "onSaveInstanceState()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "CitySelectionActivity onRestart()", Toast.LENGTH_SHORT).show();
        Log.d("CitySelectionActivity", "onRestart()");
    }
}