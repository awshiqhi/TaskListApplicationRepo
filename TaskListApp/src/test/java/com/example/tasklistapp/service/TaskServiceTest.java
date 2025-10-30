package com.example.tasklistapp.service;

import com.example.tasklistapp.Model.MyAppUser;
import com.example.tasklistapp.Model.Task;
import com.example.tasklistapp.Model.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service Tests")
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @InjectMocks
    private TaskService taskService;
    
    private MyAppUser testUser;
    private Task testTask;
    
    @BeforeEach
    void setUp() {
        testUser = new MyAppUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        testTask = new Task();
        testTask.setId("task123");
        testTask.setShortDescription("Test Task");
        testTask.setLongDescription("This is a test task description");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setUser(testUser);
    }
    
    @Test
    @DisplayName("Should get all tasks for user")
    void testGetAllTasksForUser() {
        Task task2 = new Task();
        task2.setId("task456");
        task2.setShortDescription("Another Task");
        task2.setUser(testUser);
        
        List<Task> expectedTasks = Arrays.asList(testTask, task2);
        when(taskRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId()))
            .thenReturn(expectedTasks);
        
        List<Task> actualTasks = taskService.getAllTasksForUser(testUser.getId());
        
        assertEquals(2, actualTasks.size());
        assertEquals("Test Task", actualTasks.get(0).getShortDescription());
        verify(taskRepository).findByUserIdOrderByCreatedAtDesc(testUser.getId());
    }
    
    @Test
    @DisplayName("Should get tasks by status")
    void testGetTasksByStatus() {
        when(taskRepository.findByUserIdAndStatus(testUser.getId(), Task.TaskStatus.TODO))
            .thenReturn(Arrays.asList(testTask));
        
        List<Task> tasks = taskService.getTasksByStatus(testUser.getId(), Task.TaskStatus.TODO);
        
        assertEquals(1, tasks.size());
        assertEquals(Task.TaskStatus.TODO, tasks.get(0).getStatus());
        verify(taskRepository).findByUserIdAndStatus(testUser.getId(), Task.TaskStatus.TODO);
    }
    
    @Test
    @DisplayName("Should get task by ID")
    void testGetTaskById() {
        when(taskRepository.findById("task123")).thenReturn(Optional.of(testTask));
        
        Optional<Task> foundTask = taskService.getTaskById("task123");
        
        assertTrue(foundTask.isPresent());
        assertEquals("Test Task", foundTask.get().getShortDescription());
        verify(taskRepository).findById("task123");
    }
    
    @Test
    @DisplayName("Should create task successfully")
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task createdTask = taskService.createTask(testTask, testUser);
        
        assertNotNull(createdTask);
        assertEquals(testUser, createdTask.getUser());
        verify(taskRepository).save(testTask);
    }
    
    @Test
    @DisplayName("Should update task successfully")
    void testUpdateTask() {
        Task updatedData = new Task();
        updatedData.setShortDescription("Updated Task");
        updatedData.setLongDescription("Updated description");
        updatedData.setStatus(Task.TaskStatus.IN_PROGRESS);
        
        when(taskRepository.findById("task123")).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        Task result = taskService.updateTask("task123", updatedData, testUser.getId());
        
        assertNotNull(result);
        assertEquals("Updated Task", result.getShortDescription());
        assertEquals("Updated description", result.getLongDescription());
        assertEquals(Task.TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository).save(testTask);
    }
    

    @Test
    @DisplayName("Should check if task is owned by user")
    void testIsTaskOwnedByUser() {
        when(taskRepository.findById("task123")).thenReturn(Optional.of(testTask));
        
        boolean isOwned = taskService.isTaskOwnedByUser("task123", testUser.getId());
        
        assertTrue(isOwned);
    }
    
    @Test
    @DisplayName("Should return false when task is not owned by user")
    void testIsTaskNotOwnedByUser() {
        when(taskRepository.findById("task123")).thenReturn(Optional.of(testTask));
        
        boolean isOwned = taskService.isTaskOwnedByUser("task123", 999L);
        
        assertFalse(isOwned);
    }
    
    @Test
    @DisplayName("Should return false when task does not exist")
    void testIsTaskOwnedByUserNonExistent() {
        when(taskRepository.findById("nonexistent")).thenReturn(Optional.<Task>empty());
        
        boolean isOwned = taskService.isTaskOwnedByUser("nonexistent", testUser.getId());
        
        assertFalse(isOwned);
    }
}
