package com.studenttask.service;

import com.studenttask.dto.DashboardResponse;
import com.studenttask.dto.TaskRequest;
import com.studenttask.dto.TaskResponse;
import com.studenttask.entity.Task;
import com.studenttask.entity.TaskStatus;
import com.studenttask.entity.User;
import com.studenttask.repository.TaskRepository;
import com.studenttask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, String email) {
        User user = getUserByEmail(email);
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks(String email) {
        User user = getUserByEmail(email);
        List<Task> tasks = taskRepository.findByUser(user);
        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, String email) {
        User user = getUserByEmail(email);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id, String email) {
        User user = getUserByEmail(email);
        if (!taskRepository.existsByIdAndUser(id, user)) {
            throw new IllegalArgumentException("Task not found");
        }
        taskRepository.deleteByIdAndUser(id, user);
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStats(String email) {
        User user = getUserByEmail(email);
        long totalTasks = taskRepository.countByUser(user);
        long pendingTasks = taskRepository.countByUserAndStatus(user, TaskStatus.PENDING);
        long inProgressTasks = taskRepository.countByUserAndStatus(user, TaskStatus.IN_PROGRESS);
        long completedTasks = taskRepository.countByUserAndStatus(user, TaskStatus.COMPLETED);

        return DashboardResponse.builder()
                .totalTasks(totalTasks)
                .pendingTasks(pendingTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)
                .build();
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}