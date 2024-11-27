package com.example.findfriends;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PositionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText etNumber, etName, etLatitude, etLongitude;
    private Button btnSave, btnSendSMS;
    private GoogleMap mMap;
    private Handler handler;
    private List<Position> positionsList = new ArrayList<>();
    private ApiServiceManager apiServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        etNumber = findViewById(R.id.etNumber);
        etName = findViewById(R.id.etName);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        btnSave = findViewById(R.id.btnSave);
        btnSendSMS = findViewById(R.id.btnSendSMS);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error initializing map fragment", Toast.LENGTH_SHORT).show();
        }

        apiServiceManager = new ApiServiceManager();
        startLocationUpdates();

        btnSave.setOnClickListener(v -> savePosition());
        btnSendSMS.setOnClickListener(v -> sendPositionSMS());
    }

    private void startLocationUpdates() {
        handler = new Handler();
        handler.postDelayed(() -> startLocationUpdates(),  60 * 1000); // 1 minute
    }

    private void savePosition() {
        String number = etNumber.getText().toString();
        String name = etName.getText().toString();
        double latitude = Double.parseDouble(etLatitude.getText().toString());
        double longitude = Double.parseDouble(etLongitude.getText().toString());

        Position newPosition = new Position(name, number, latitude, longitude);

        apiServiceManager.createPosition(newPosition, new ApiServiceManager.PositionCallback() {
            @Override
            public void onSuccess(Position position) {
                positionsList.add(position);
                Toast.makeText(PositionActivity.this, "Position saved: " + position.getName(), Toast.LENGTH_SHORT).show();
                clearInputs();
                navigateToListPositions();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(PositionActivity.this, "Failed to save position: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPositionSMS() {
        String number = etNumber.getText().toString();
        String latitude = etLatitude.getText().toString();
        String longitude = etLongitude.getText().toString();
        String message = "Position: Latitude " + latitude + ", Longitude " + longitude;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(this, "SMS sent!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS failed to send.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        etNumber.setText("");
        etName.setText("");
        etLatitude.setText("");
        etLongitude.setText("");
    }

    private void navigateToListPositions() {
        Intent intent = new Intent(PositionActivity.this, ListPositionsActivity.class);
        intent.putExtra("positions_list", (Serializable) positionsList);
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Position"));
            etLatitude.setText(String.valueOf(latLng.latitude));
            etLongitude.setText(String.valueOf(latLng.longitude));
        });
    }
}
