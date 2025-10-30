package com.example.tasklistapp.Controller;

import com.example.tasklistapp.Model.MyAppUser;
import com.example.tasklistapp.Model.MyAppUserRepository;
import com.example.tasklistapp.Model.Task;
import com.example.tasklistapp.Model.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Task Controller Integration Tests")
class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    private MyAppUser testUser;
    private Task testTask;
    
    @BeforeEach
    void setUp() {
        // Clean up
        taskRepository.deleteAll();
        
        // Create test user
        testUser = new MyAppUser();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedpassword");
        testUser.setVerified(true);
        testUser = userRepository.save(testUser);
        
        // Create test task
        testTask = new Task();
        testTask.setShortDescription("Test Task");
        testTask.setLongDescription("This is a test task");
        testTask.setStatus(Task.TaskStatus.TODO);
        testTask.setUser(testUser);
        testTask = taskRepository.save(testTask);
    }
    
    @Test
    @DisplayName("Should get all tasks for authenticated user")
    @WithMockUser(username = "testuser")
    void testGetAllTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].shortDescription").value("Test Task"))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }
    
    @Test
    @DisplayName("Should get task by ID")
    @WithMockUser(username = "testuser")
    void testGetTaskById() throws Exception {
        mockMvc.perform(get("/tasks/{id}", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortDescription").value("Test Task"))
                .andExpect(jsonPath("$.longDescription").value("This is a test task"));
    }
    
    @Test
    @DisplayName("Should return 404 for non-existent task")
    @WithMockUser(username = "testuser")
    void testGetNonExistentTask() throws Exception {
        mockMvc.perform(get("/tasks/{id}", "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
    
    @Test
    @DisplayName("Should create new task")
    @WithMockUser(username = "testuser")
    void testCreateTask() throws Exception {
        Task newTask = new Task();
        newTask.setShortDescription("New Task");
        newTask.setLongDescription("New task description");
        newTask.setStatus(Task.TaskStatus.TODO);
        
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortDescription").value("New Task"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }
    
    @Test
    @DisplayName("Should update existing task")
    @WithMockUser(username = "testuser")
    void testUpdateTask() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setShortDescription("Updated Task");
        updatedTask.setLongDescription("Updated description");
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        
        mockMvc.perform(put("/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortDescription").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }
    
    @Test
    @DisplayName("Should return 404 when updating non-existent task")
    @WithMockUser(username = "testuser")
    void testUpdateNonExistentTask() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setShortDescription("Updated Task");
        
        mockMvc.perform(put("/tasks/{id}", "nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should delete task")
    @WithMockUser(username = "testuser")
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));
    }
    
    @Test
    @DisplayName("Should return 404 when deleting non-existent task")
    @WithMockUser(username = "testuser")
    void testDeleteNonExistentTask() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", "nonexistent"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should redirect to login without authentication")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @DisplayName("Should not allow user to access another user's task")
    @WithMockUser(username = "otheruser")
    void testAccessOtherUserTask() throws Exception {
        // Create another user
        MyAppUser otherUser = new MyAppUser();
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setPassword("password");
        otherUser.setVerified(true);
        userRepository.save(otherUser);
        
        mockMvc.perform(get("/tasks/{id}", testTask.getId()))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("Should update task status to IN_PROGRESS")
    @WithMockUser(username = "testuser")
    void testUpdateTaskStatusToInProgress() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setShortDescription(testTask.getShortDescription());
        updatedTask.setLongDescription(testTask.getLongDescription());
        updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
        
        mockMvc.perform(put("/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }
    
    @Test
    @DisplayName("Should update task status to DONE")
    @WithMockUser(username = "testuser")
    void testUpdateTaskStatusToDone() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setShortDescription(testTask.getShortDescription());
        updatedTask.setLongDescription(testTask.getLongDescription());
        updatedTask.setStatus(Task.TaskStatus.DONE);
        
        mockMvc.perform(put("/tasks/{id}", testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }
    
    @Test
    @DisplayName("Should create task with long description")
    @WithMockUser(username = "testuser")
    void testCreateTaskWithLongDescription() throws Exception {
        String longDescription = "This is a very long description. ".repeat(50);
        
        Task newTask = new Task();
        newTask.setShortDescription("Task with long description");
        newTask.setLongDescription(longDescription);
        newTask.setStatus(Task.TaskStatus.TODO);
        
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.longDescription").value(longDescription));
    }
}
