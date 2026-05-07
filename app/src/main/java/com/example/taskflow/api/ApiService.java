package com.example.taskflow.api;

import com.example.taskflow.models.LoginRequest;
import com.example.taskflow.models.LoginResponse;
import com.example.taskflow.models.MessageResponse;
import com.example.taskflow.models.TaskRequest;
import com.example.taskflow.models.TaskResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Auth endpoints
    @POST("api/auth/register")
    Call<MessageResponse> register(@Body LoginRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Task endpoints (all require JWT token)
    @GET("api/tasks/")
    Call<TaskResponse> getTasks(@Header("Authorization") String token);

    @POST("api/tasks/")
    Call<MessageResponse> createTask(
            @Header("Authorization") String token,
            @Body TaskRequest request
    );

    @PUT("api/tasks/{id}")
    Call<MessageResponse> updateTask(
            @Header("Authorization") String token,
            @Path("id") int taskId,
            @Body TaskRequest request
    );

    @DELETE("api/tasks/{id}")
    Call<MessageResponse> deleteTask(
            @Header("Authorization") String token,
            @Path("id") int taskId
    );
}
