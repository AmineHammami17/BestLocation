package com.example.findfriends;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.findfriends.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationUpdater locationUpdater;

    private static final int PERMISSION_REQUEST_CODE = 1;

    // Define required permissions
    private static final List<String> REQUIRED_PERMISSIONS = new ArrayList<>();

    private ActivityMainBinding binding;

    static {
        REQUIRED_PERMISSIONS.add(Manifest.permission.SEND_SMS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SMS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.RECEIVE_SMS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);

        // Add POST_NOTIFICATIONS permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            REQUIRED_PERMISSIONS.add(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize LocationUpdater
        locationUpdater = new LocationUpdater(this);

        // Set up the binding and layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up navigation
        setupNavigation();

        // Request necessary permissions
        requestPermissions();

        // Set up button listeners
        setupButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationUpdater.startUpdatingPosition();
    }


    private void setupNavigation() {
        try {
            BottomNavigationView navView = findViewById(R.id.nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            ).build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        } catch (Exception e) {
            Log.e("MainActivity", "Navigation setup error: " + e.getMessage());
        }
    }

    private void requestPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void setupButtons() {
        // Navigate to PositionActivity
        Button btnOpenPositionActivity = findViewById(R.id.btnOpenPositionActivity);
        if (btnOpenPositionActivity != null) {
            btnOpenPositionActivity.setOnClickListener(v -> openActivity(PositionActivity.class));
        }

        // Navigate to ListPositionsActivity
        Button btnShowSavedPositions = findViewById(R.id.btnShowSavedPositions);
        if (btnShowSavedPositions != null) {
            btnShowSavedPositions.setOnClickListener(v -> openActivity(ListPositionsActivity.class));
        }
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    showPermissionDeniedDialog();
                    return;
                }
            }
            Log.d("MainActivity", "All permissions granted.");
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("All permissions are required to use this app. Please grant them.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
