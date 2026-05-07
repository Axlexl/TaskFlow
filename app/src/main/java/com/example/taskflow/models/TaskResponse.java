package com.example.taskflow.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TaskResponse {

    @SerializedName("tasks")
    private List<Task> tasks;

    public List<Task> getTasks() { return tasks; }
}
