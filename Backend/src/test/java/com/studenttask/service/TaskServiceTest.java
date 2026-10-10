package com.studenttask.service;

import com.studenttask.dto.DashboardResponse;
import com.studenttask.dto.TaskRequest;
import com.studenttask.dto.TaskResponse;
import com.studenttask.entity.Task;
import com.studenttask.entity.TaskPriority;
import com.studenttask.entity.TaskStatus;
import com.studenttask.entity.User;
import com.studenttask.repository.TaskRepository;
import com.studenttask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;
    private TaskRequest taskRequest;
    private String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email(userEmail)
                .password("hashedPassword")
                .build();

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(1))
                .user(testUser)
                .build();

        taskRequest = TaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void createTask_ValidRequest_ReturnsTaskResponse() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TaskResponse response = taskService.createTask(taskRequest, userEmail);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertEquals(TaskStatus.PENDING, response.getStatus());
        assertEquals(TaskPriority.HIGH, response.getPriority());

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(taskRequest, userEmail));

        assertEquals("User not found", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getAllTasks_ReturnsUserTasks() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUser(testUser)).thenReturn(Collections.singletonList(testTask));

        List<TaskResponse> tasks = taskService.getAllTasks(userEmail);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByUser(testUser);
    }

    @Test
    void getTaskById_OwnedTask_ReturnsTaskResponse() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));

        TaskResponse response = taskService.getTaskById(1L, userEmail);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Task", response.getTitle());

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).findByIdAndUser(1L, testUser);
    }

    @Test
    void getTaskById_NotOwned_ThrowsException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.getTaskById(1L, userEmail));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository).findByIdAndUser(1L, testUser);
    }

    @Test
    void updateTask_OwnedTask_ReturnsUpdatedTask() {
        TaskRequest updateRequest = TaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTask(1L, updateRequest, userEmail);

        assertNotNull(response);
        assertEquals("Updated Task", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
        assertEquals(TaskPriority.MEDIUM, response.getPriority());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_NotOwned_ThrowsException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTask(1L, taskRequest, userEmail));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_OwnedTask_DeletesSuccessfully() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.existsByIdAndUser(1L, testUser)).thenReturn(true);
        doNothing().when(taskRepository).deleteByIdAndUser(1L, testUser);

        assertDoesNotThrow(() -> taskService.deleteTask(1L, userEmail));

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).existsByIdAndUser(1L, testUser);
        verify(taskRepository).deleteByIdAndUser(1L, testUser);
    }

    @Test
    void deleteTask_NotOwned_ThrowsException() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.existsByIdAndUser(1L, testUser)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.deleteTask(1L, userEmail));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, never()).deleteByIdAndUser(anyLong(), any(User.class));
    }

    @Test
    void getDashboardStats_ReturnsCorrectCounts() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.countByUser(testUser)).thenReturn(10L);
        when(taskRepository.countByUserAndStatus(testUser, TaskStatus.PENDING)).thenReturn(4L);
        when(taskRepository.countByUserAndStatus(testUser, TaskStatus.IN_PROGRESS)).thenReturn(3L);
        when(taskRepository.countByUserAndStatus(testUser, TaskStatus.COMPLETED)).thenReturn(3L);

        DashboardResponse stats = taskService.getDashboardStats(userEmail);

        assertNotNull(stats);
        assertEquals(10L, stats.getTotalTasks());
        assertEquals(4L, stats.getPendingTasks());
        assertEquals(3L, stats.getInProgressTasks());
        assertEquals(3L, stats.getCompletedTasks());

        verify(userRepository).findByEmail(userEmail);
        verify(taskRepository).countByUser(testUser);
        verify(taskRepository).countByUserAndStatus(testUser, TaskStatus.PENDING);
        verify(taskRepository).countByUserAndStatus(testUser, TaskStatus.IN_PROGRESS);
        verify(taskRepository).countByUserAndStatus(testUser, TaskStatus.COMPLETED);
    }

    @Test
    void createTask_SetsCorrectUser() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TaskResponse response = taskService.createTask(taskRequest, userEmail);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertEquals(testUser, savedTask.getUser());
    }
}