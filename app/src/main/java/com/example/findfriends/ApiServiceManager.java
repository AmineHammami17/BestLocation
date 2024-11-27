package com.example.findfriends;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiServiceManager {
    private final ApiService apiService;

    public ApiServiceManager() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public interface PositionsCallback {
        void onSuccess(List<Position> positions);
        void onError(String errorMessage);
    }

    public interface PositionCallback {
        void onSuccess(Position position);
        void onError(String errorMessage);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public void fetchPositions(PositionsCallback callback) {
        Call<List<Position>> call = apiService.getPositions();
        call.enqueue(createCallback(callback));
    }

    public void createPosition(Position position, PositionCallback callback) {
        Call<Position> call = apiService.createPosition(position);
        call.enqueue(createCallback(callback));
    }

    public void updatePosition(String id, Position position, Context context, PositionCallback callback) {
        Call<Position> call = apiService.updatePosition(id, position);

        call.enqueue(new Callback<Position>() {
            @Override
            public void onResponse(Call<Position> call, Response<Position> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    Toast.makeText(context, "Position updated", Toast.LENGTH_SHORT).show();
                } else {
                    callback.onError("Failed to update position: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Position> call, Throwable t) {
                callback.onError("Error: " + t.getMessage());
            }
        });
    }


    public void deletePosition(String id, SimpleCallback callback) {
        Call<Void> call = apiService.deletePosition(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to delete position: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Error: " + t.getMessage());
            }
        });
    }

    private <T> Callback<T> createCallback(Object callback) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (callback instanceof PositionsCallback) {
                        ((PositionsCallback) callback).onSuccess((List<Position>) response.body());
                    } else if (callback instanceof PositionCallback) {
                        ((PositionCallback) callback).onSuccess((Position) response.body());
                    }
                } else {
                    String errorMessage = "Error: " + response.message();
                    if (callback instanceof PositionsCallback) {
                        ((PositionsCallback) callback).onError(errorMessage);
                    } else if (callback instanceof PositionCallback) {
                        ((PositionCallback) callback).onError(errorMessage);
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                String errorMessage = "Error: " + t.getMessage();
                if (callback instanceof PositionsCallback) {
                    ((PositionsCallback) callback).onError(errorMessage);
                } else if (callback instanceof PositionCallback) {
                    ((PositionCallback) callback).onError(errorMessage);
                }
            }
        };
    }
}
