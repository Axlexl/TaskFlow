package com.example.taskflow;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskflow.api.ApiClient;
import com.example.taskflow.models.MessageResponse;
import com.example.taskflow.models.TaskRequest;
import com.example.taskflow.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Spinner spinnerStatus;
    private Button btnSave;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    private boolean isEditMode = false;
    private int taskId = -1;

    private final String[] statusLabels = {"Pending", "In Progress", "Done"};
    private final String[] statusValues = {"pending", "in_progress", "done"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        sessionManager = new SessionManager(this);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        // Setup status spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, statusLabels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);

        // Check if editing existing task
        if (getIntent().hasExtra("task_id")) {
            isEditMode = true;
            taskId = getIntent().getIntExtra("task_id", -1);
            String title = getIntent().getStringExtra("task_title");
            String description = getIntent().getStringExtra("task_description");
            String status = getIntent().getStringExtra("task_status");

            etTitle.setText(title);
            etDescription.setText(description);
            setSpinnerStatus(status);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Task");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Add Task");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void setSpinnerStatus(String status) {
        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i].equals(status)) {
                spinnerStatus.setSelection(i);
                return;
            }
        }
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        int selectedIndex = spinnerStatus.getSelectedItemPosition();
        String status = statusValues[selectedIndex];

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        TaskRequest request = new TaskRequest(title, description, status);

        if (isEditMode) {
            ApiClient.getApiService().updateTask(sessionManager.getToken(), taskId, request)
                    .enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditTaskActivity.this, "Task updated!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEditTaskActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            Toast.makeText(AddEditTaskActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            ApiClient.getApiService().createTask(sessionManager.getToken(), request)
                    .enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(AddEditTaskActivity.this, "Task created!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEditTaskActivity.this, "Failed to create task", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            Toast.makeText(AddEditTaskActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
