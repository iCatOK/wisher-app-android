package ru.omegapps.wisherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import ru.omegapps.wisherapp.data_agents.FireBaseDataAgent;
import ru.omegapps.wisherapp.fragments.HomeFragment;
import ru.omegapps.wisherapp.fragments.WishBlocksFragment;
import ru.omegapps.wisherapp.fragments.auth.AuthorizatonFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    FireBaseDataAgent dataAgent;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.up_right_corner_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_log_out) {
            Toast.makeText(this, "Logouting", Toast.LENGTH_SHORT).show();
            FireBaseDataAgent.logout();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_wisher, new AuthorizatonFragment())
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FireBaseDataAgent.init();

        bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(R.id.menu_wishes);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selected = null;
            switch (item.getItemId()){
                case R.id.menu_wishes:
                    selected = new HomeFragment();
                    break;
                case R.id.menu_wish_blocks:
                    selected = new WishBlocksFragment();
                    break;
            }
            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_wisher, selected)
                        .commit();
            }

            return true;
        });

    }
}