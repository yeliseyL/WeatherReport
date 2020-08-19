package elisey.lobanov.weatherreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button citySelectBtn = findViewById(R.id.button4);

        citySelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CitySelectionActivity.class));
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
        Log.d("MainActivity", instanceState + " - onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "MainActivity onStart()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onStart()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState){
        super.onRestoreInstanceState(saveInstanceState);
        Toast.makeText(getApplicationContext(), "Повторный запуск!! - onRestoreInstanceState()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "Повторный запуск!! - onRestoreInstanceState()");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "MainActivity onStop()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "MainActivity onDestroy()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "MainActivity onPause()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        final MainPresenter presenter = MainPresenter.getInstance();
        final TextView windSpeedTextView = findViewById(R.id.windSpeedTextView);
        final TextView atmPressureTextView = findViewById(R.id.atmPressureTextView);
        final TextView cityName = findViewById(R.id.textView);

        if (presenter.isShowWindSpeed()) {
            windSpeedTextView.setVisibility(View.VISIBLE);
        } else {
            windSpeedTextView.setVisibility(View.GONE);
        }

        if (presenter.isShowAtmPressure()) {
            atmPressureTextView.setVisibility(View.VISIBLE);
        } else {
            atmPressureTextView.setVisibility(View.GONE);
        }

        if (!presenter.getCityName().equals("")) {
            cityName.setText(presenter.getCityName());
        }

        Toast.makeText(getApplicationContext(), "MainActivity onResume()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onResume()");
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        Toast.makeText(getApplicationContext(), "MainActivity onSaveInstanceState()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onSaveInstanceState()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "MainActivity onRestart()", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onRestart()");
    }
}
