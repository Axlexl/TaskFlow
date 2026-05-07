package com.example.taskflow.models;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("task_id")
    private int taskId;

    public String getMessage() { return message; }
    public int getTaskId() { return taskId; }
}
