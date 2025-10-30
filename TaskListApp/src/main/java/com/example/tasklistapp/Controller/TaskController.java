package com.example.tasklistapp.Controller;

import com.example.tasklistapp.Model.MyAppUser;
import com.example.tasklistapp.Model.MyAppUserRepository;
import com.example.tasklistapp.Model.Task;
import com.example.tasklistapp.exception.ResourceNotFoundException;
import com.example.tasklistapp.exception.UnauthorizedException;
import com.example.tasklistapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    private MyAppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Task>> getAllTasks() {
        MyAppUser currentUser = getCurrentUser();
        List<Task> tasks = taskService.getAllTasksForUser(currentUser.getId());
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        MyAppUser currentUser = getCurrentUser();
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        
        if (!taskService.isTaskOwnedByUser(id, currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to access this task");
        }
        
        return ResponseEntity.ok(task);
    }
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        MyAppUser currentUser = getCurrentUser();
        Task createdTask = taskService.createTask(task, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        MyAppUser currentUser = getCurrentUser();
        Task updatedTask = taskService.updateTask(id, task, currentUser.getId());
        return ResponseEntity.ok(updatedTask);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable String id) {
        MyAppUser currentUser = getCurrentUser();
        taskService.deleteTask(id, currentUser.getId());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Task deleted successfully");
        return ResponseEntity.ok(response);
    }
}
