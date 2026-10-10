package com.studenttask.repository;

import com.studenttask.entity.Task;
import com.studenttask.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);

    Optional<Task> findByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);

    boolean existsByIdAndUser(Long id, User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.status = :status")
    long countByUserAndStatus(@Param("user") User user, @Param("status") com.studenttask.entity.TaskStatus status);
}