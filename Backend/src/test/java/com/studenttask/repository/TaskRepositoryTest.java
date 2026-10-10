package com.studenttask.repository;

import com.studenttask.entity.Task;
import com.studenttask.entity.TaskPriority;
import com.studenttask.entity.TaskStatus;
import com.studenttask.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.extension.ExtendWith(SpringExtension.class)
@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // Valid BCrypt hash for "password123"
        String validBcrypt = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password(validBcrypt)
                .build();
        testUser = entityManager.persistAndFlush(testUser);

        anotherUser = User.builder()
                .name("Another User")
                .email("another@example.com")
                .password(validBcrypt)
                .build();
        anotherUser = entityManager.persistAndFlush(anotherUser);
    }

    @Test
    void findByUser_ReturnsOnlyOwnedTasks() {
        Task task1 = Task.builder()
                .title("Task 1")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task1);

        Task task2 = Task.builder()
                .title("Task 2")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .user(anotherUser)
                .build();
        entityManager.persistAndFlush(task2);

        List<Task> userTasks = taskRepository.findByUser(testUser);

        assertEquals(1, userTasks.size());
        assertEquals("Task 1", userTasks.get(0).getTitle());
    }

    @Test
    void findByIdAndUser_OwnedTask_ReturnsTask() {
        Task task = Task.builder()
                .title("Test Task")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task);

        Optional<Task> found = taskRepository.findByIdAndUser(task.getId(), testUser);

        assertTrue(found.isPresent());
        assertEquals(task.getId(), found.get().getId());
    }

    @Test
    void findByIdAndUser_NotOwned_ReturnsEmpty() {
        Task task = Task.builder()
                .title("Test Task")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(anotherUser)
                .build();
        entityManager.persistAndFlush(task);

        Optional<Task> found = taskRepository.findByIdAndUser(task.getId(), testUser);

        assertFalse(found.isPresent());
    }

    @Test
    void deleteByIdAndUser_OwnedTask_DeletesSuccessfully() {
        Task task = Task.builder()
                .title("Test Task")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task);

        taskRepository.deleteByIdAndUser(task.getId(), testUser);

        Optional<Task> found = taskRepository.findByIdAndUser(task.getId(), testUser);
        assertFalse(found.isPresent());
    }

    @Test
    void deleteByIdAndUser_NotOwned_DoesNotDelete() {
        Task task = Task.builder()
                .title("Test Task")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(anotherUser)
                .build();
        entityManager.persistAndFlush(task);

        taskRepository.deleteByIdAndUser(task.getId(), testUser);

        Optional<Task> found = taskRepository.findByIdAndUser(task.getId(), anotherUser);
        assertTrue(found.isPresent());
    }

    @Test
    void countByUser_ReturnsCorrectCount() {
        Task task1 = Task.builder()
                .title("Task 1")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task1);

        Task task2 = Task.builder()
                .title("Task 2")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task2);

        Task task3 = Task.builder()
                .title("Task 3")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .user(anotherUser)
                .build();
        entityManager.persistAndFlush(task3);

        long count = taskRepository.countByUser(testUser);

        assertEquals(2L, count);
    }

    @Test
    void countByUserAndStatus_ReturnsCorrectCount() {
        Task task1 = Task.builder()
                .title("Task 1")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task1);

        Task task2 = Task.builder()
                .title("Task 2")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task2);

        Task task3 = Task.builder()
                .title("Task 3")
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.LOW)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task3);

        long pendingCount = taskRepository.countByUserAndStatus(testUser, com.studenttask.entity.TaskStatus.PENDING);
        long inProgressCount = taskRepository.countByUserAndStatus(testUser, com.studenttask.entity.TaskStatus.IN_PROGRESS);
        long completedCount = taskRepository.countByUserAndStatus(testUser, com.studenttask.entity.TaskStatus.COMPLETED);

        assertEquals(1L, pendingCount);
        assertEquals(1L, inProgressCount);
        assertEquals(1L, completedCount);
    }

    @Test
    void existsByIdAndUser_OwnedTask_ReturnsTrue() {
        Task task = Task.builder()
                .title("Test Task")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(testUser)
                .build();
        entityManager.persistAndFlush(task);

        boolean exists = taskRepository.existsByIdAndUser(task.getId(), testUser);

        assertTrue(exists);
    }

    @Test
    void existsByIdAndUser_NotOwned_ReturnsFalse() {
        Task task = Task.builder()
                .title("Test Task")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.HIGH)
                .user(anotherUser)
                .build();
        entityManager.persistAndFlush(task);

        boolean exists = taskRepository.existsByIdAndUser(task.getId(), testUser);

        assertFalse(exists);
    }
}