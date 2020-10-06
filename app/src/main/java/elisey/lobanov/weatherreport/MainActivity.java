package elisey.lobanov.weatherreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.Provider;

public class MainActivity extends AppCompatActivity implements Constants, NavigationView.OnNavigationItemSelectedListener {
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private CityChooserParcel parcel;
    private BroadcastReceiver NetworkStatusReciever;
    private BroadcastReceiver BatteryLevelReciever;
    private SharedPreferences sharedPref;
    private static final int PERMISSION_REQUEST_CODE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_layout);
        setTitle("Weather Report");

        initBroadcastRecievers();

        drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        requestPemissions();

        parcel = CityChooserParcel.getInstance();

        Fragment fragmentMain = MainFragment.create(parcel);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragmentMain)
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (id) {
            case R.id.action_settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage(R.string.info_message)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.home_item:
                parcel = CityChooserParcel.getInstance();

                Fragment fragmentMain = MainFragment.create(parcel);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, fragmentMain)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.history_item:
                Fragment historyFragment = new HistoryFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, historyFragment)
                        .addToBackStack(null)
                        .commit();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.about_item:
                new AlertDialog.Builder(this)
                        .setTitle("Info")
                        .setMessage(R.string.info_message)
                        .show();
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }
        return true;
    }

    private void initBroadcastRecievers() {
        NetworkStatusReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                    return;
                } else {
                    Toast.makeText(context, R.string.connection_lost, Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(NetworkStatusReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        BatteryLevelReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                if (level < 10) {
                    Toast.makeText(context, R.string.battery_low, Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(BatteryLevelReciever, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void requestPemissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getAndSaveCoordinates();
        } else {
            requestLocationPermissions();
        }
    }

    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void getAndSaveCoordinates() {


        sharedPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(NetworkStatusReciever);
        unregisterReceiver(BatteryLevelReciever);
    }
}