package elisey.lobanov.weatherreport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import elisey.lobanov.weatherreport.Connection.OnlineConnection;
import elisey.lobanov.weatherreport.Connection.WeatherRequest;

public class MainActivity extends AppCompatActivity implements Constants, NavigationView.OnNavigationItemSelectedListener{
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private CityChooserParcel parcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_layout);
        setTitle("Weather Report");

        drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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




}