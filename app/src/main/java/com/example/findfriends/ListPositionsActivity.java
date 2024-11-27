package com.example.findfriends;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListPositionsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPositions;
    private Button btnDeletePosition, btnShowOnMap, btnSendSms;
    private List<Position> positionsList = new ArrayList<>();
    private PositionsAdapter positionAdapter;
    private ApiServiceManager apiServiceManager;
    private Position selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listpositions);

        recyclerViewPositions = findViewById(R.id.recyclerViewPositions);
        btnDeletePosition = findViewById(R.id.btnDeletePosition);
        btnShowOnMap = findViewById(R.id.btnShowOnMap);
        btnSendSms = findViewById(R.id.btnSendSms);

        recyclerViewPositions.setLayoutManager(new LinearLayoutManager(this));

        positionAdapter = new PositionsAdapter(positionsList, position -> {
            selectedPosition = position;
            enableActionButtons(true);
        });

        recyclerViewPositions.setAdapter(positionAdapter);

        apiServiceManager = new ApiServiceManager();
        fetchPositions();

        enableActionButtons(false);

        btnDeletePosition.setOnClickListener(v -> deletePosition(selectedPosition));
        btnShowOnMap.setOnClickListener(v -> showOnMap(selectedPosition));
        btnSendSms.setOnClickListener(v -> sendSms(selectedPosition));
    }

    private void fetchPositions() {
        apiServiceManager.fetchPositions(new ApiServiceManager.PositionsCallback() {
            @Override
            public void onSuccess(List<Position> positions) {
                Toast.makeText(ListPositionsActivity.this, "Positions fetched: " + positions.size(), Toast.LENGTH_SHORT).show();
                positionsList.clear();
                positionsList.addAll(positions);
                positionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ListPositionsActivity.this, "Failed to fetch positions: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableActionButtons(boolean isEnabled) {
        btnDeletePosition.setEnabled(isEnabled);
        btnShowOnMap.setEnabled(isEnabled);
        btnSendSms.setEnabled(isEnabled);
    }

    private void deletePosition(Position position) {
        if (position != null) {
            apiServiceManager.deletePosition(position.getId(), new ApiServiceManager.SimpleCallback() {
                @Override
                public void onSuccess() {
                    positionsList.remove(position);
                    positionAdapter.notifyDataSetChanged();
                    Toast.makeText(ListPositionsActivity.this, "Position deleted", Toast.LENGTH_SHORT).show();
                    enableActionButtons(false);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ListPositionsActivity.this, "Failed to delete position: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No position selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOnMap(Position position) {
        if (position != null) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("longitude", String.valueOf(position.getLongitude())); // Correct key name
            intent.putExtra("latitude", String.valueOf(position.getLatitude())); // Correct key name
            startActivity(intent);
        } else {
            Toast.makeText(this, "No position selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSms(Position position) {
        if (position != null) {
            String phoneNumber = position.getPhoneNumber();
            String message = "Hey, I found you at this position!";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No position selected", Toast.LENGTH_SHORT).show();
        }
    }
}
