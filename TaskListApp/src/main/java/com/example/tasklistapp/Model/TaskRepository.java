package com.example.tasklistapp.Model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    
    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Task> findByUserIdAndStatus(Long userId, Task.TaskStatus status);
    
}
