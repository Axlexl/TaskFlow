package com.example.taskflow;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskflow.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvStatus;
        ImageButton btnEdit, btnDelete;

        TaskViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Task task) {
            tvTitle.setText(task.getTitle());

            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                tvDescription.setText(task.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            tvStatus.setText(formatStatus(task.getStatus()));
            tvStatus.setTextColor(getStatusColor(task.getStatus()));

            btnEdit.setOnClickListener(v -> listener.onEditClick(task));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(task));
        }

        private String formatStatus(String status) {
            if (status == null) return "Pending";
            switch (status) {
                case "in_progress": return "In Progress";
                case "done": return "Done";
                default: return "Pending";
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return Color.parseColor("#FF9800");
            switch (status) {
                case "in_progress": return Color.parseColor("#2196F3");
                case "done": return Color.parseColor("#4CAF50");
                default: return Color.parseColor("#FF9800");
            }
        }
    }
}
