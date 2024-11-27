package com.example.findfriends;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocationUpdater {
    private final FusedLocationProviderClient fusedLocationClient;
    private final ApiServiceManager apiServiceManager;
    private final Context context;
    private static final String POSITION_ID = "1";
    private static final long UPDATE_INTERVAL = TimeUnit.SECONDS.toMillis(30);
    public LocationUpdater(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.apiServiceManager = new ApiServiceManager();
    }

    public void startUpdatingPosition() {
        fetchAndUpdatePosition();
    }

    private void fetchAndUpdatePosition() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                apiServiceManager.fetchPositions(new ApiServiceManager.PositionsCallback() {
                    @Override
                    public void onSuccess(List<Position> positions) {
                        Position positionToUpdate = null;
                        for (Position position : positions) {
                            if (position.getId().equals(POSITION_ID)) {
                                positionToUpdate = position;
                                break;
                            }
                        }

                        if (positionToUpdate != null) {
                            LocationRequest locationRequest = LocationRequest.create()
                                    .setInterval(UPDATE_INTERVAL)
                                    .setFastestInterval(UPDATE_INTERVAL / 2)
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(context, "Location permissions are not granted!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Position finalPositionToUpdate = positionToUpdate;
                            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    if (locationResult == null) return;
                                    Location currentLocation = locationResult.getLastLocation();
                                    if (currentLocation != null) {
                                        // Update the position with the new location
                                        finalPositionToUpdate.setLat(String.valueOf(currentLocation.getLatitude()));
                                        finalPositionToUpdate.setLongitude(String.valueOf(currentLocation.getLongitude()));
                                        updatePosition(finalPositionToUpdate);
                                    }
                                }
                            }, context.getMainLooper());
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(context, "Failed to fetch positions: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                // Schedule next update in 10 seconds
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };

        handler.post(runnable);
    }

    private void updatePosition(Position position) {
        apiServiceManager.updatePosition(POSITION_ID, position, context, new ApiServiceManager.PositionCallback() {
            @Override
            public void onSuccess(Position updatedPosition) {
                Toast.makeText(context, "Position updated: Lat " + updatedPosition.getLatitude() + ", Long " + updatedPosition.getLongitude(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, "Failed to update position: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
