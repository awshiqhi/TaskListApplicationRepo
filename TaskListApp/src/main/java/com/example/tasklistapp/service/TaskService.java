package com.example.tasklistapp.service;

import com.example.tasklistapp.Model.MyAppUser;
import com.example.tasklistapp.Model.Task;
import com.example.tasklistapp.Model.TaskRepository;
import com.example.tasklistapp.exception.ResourceNotFoundException;
import com.example.tasklistapp.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public List<Task> getAllTasksForUser(Long userId) {
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Task> getTasksByStatus(Long userId, Task.TaskStatus status) {
        return taskRepository.findByUserIdAndStatus(userId, status);
    }
    
    public Optional<Task> getTaskById(String taskId) {
        return taskRepository.findById(taskId);
    }
    
    @Transactional
    public Task createTask(Task task, MyAppUser user) {
        task.setUser(user);
        return taskRepository.save(task);
    }
    
    @Transactional
    public Task updateTask(String taskId, Task updatedTask, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        if (!task.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this task");
        }
        
        task.setShortDescription(updatedTask.getShortDescription());
        task.setLongDescription(updatedTask.getLongDescription());
        task.setStatus(updatedTask.getStatus());
        return taskRepository.save(task);
    }
    
    @Transactional
    public void deleteTask(String taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));
        
        if (!task.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this task");
        }
        
        taskRepository.deleteById(taskId);
    }
    
    public boolean isTaskOwnedByUser(String taskId, Long userId) {
        Optional<Task> task = taskRepository.findById(taskId);
        return task.isPresent() && task.get().getUser().getId().equals(userId);
    }
}
