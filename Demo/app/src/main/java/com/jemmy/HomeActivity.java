package com.jemmy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jemmy.utils.SharedPreferencesUtil;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(bottomNavigationView.getMenu()).build();
        NavController controller = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, controller, configuration);
        NavigationUI.setupWithNavController(bottomNavigationView, controller);

        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(getApplicationContext());

    }
}
