package com.example.findfriends;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("positions")
    Call<List<Position>> getPositions();

    @POST("positions")
    Call<Position> createPosition(@Body Position position);

    @PUT("positions/{id}")
    Call<Position> updatePosition(@Path("id") String id, @Body Position position);

    @DELETE("positions/{id}")
    Call<Void> deletePosition(@Path("id") String id);
}
