// Task Management JavaScript
let tasks = [];
let currentFilter = 'all';
let editingTaskId = null;

// DOM Elements
const tasksGrid = document.getElementById('tasksGrid');
const taskModal = document.getElementById('taskModal');
const viewTaskModal = document.getElementById('viewTaskModal');
const taskForm = document.getElementById('taskForm');
const createTaskBtn = document.getElementById('createTaskBtn');
const closeBtn = document.querySelector('.close');
const viewCloseBtn = document.querySelectorAll('.view-close');
const cancelBtn = document.getElementById('cancelBtn');
const modalTitle = document.getElementById('modalTitle');
const statusFilter = document.getElementById('statusFilter');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadTasks();
    setupEventListeners();
});

// Event Listeners
function setupEventListeners() {
    createTaskBtn.addEventListener('click', () => openCreateModal());
    closeBtn.addEventListener('click', () => closeModal());
    cancelBtn.addEventListener('click', () => closeModal());
    taskForm.addEventListener('submit', handleFormSubmit);
    
    viewCloseBtn.forEach(btn => {
        btn.addEventListener('click', () => closeViewModal());
    });
    
    // Dropdown filter listener
    statusFilter.addEventListener('change', (e) => {
        currentFilter = e.target.value;
        renderTasks();
    });
    
    window.addEventListener('click', (e) => {
        if (e.target === taskModal) closeModal();
        if (e.target === viewTaskModal) closeViewModal();
    });
}

// API Calls
async function loadTasks() {
    try {
        const response = await fetch('/tasks');
        if (response.ok) {
            tasks = await response.json();
            renderTasks();
        } else {
            showError('Failed to load tasks');
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
        showError('Error loading tasks');
    }
}

async function createTask(taskData) {
    try {
        const response = await fetch('/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            const newTask = await response.json();
            tasks.unshift(newTask);
            renderTasks();
            closeModal();
        } else {
            showError('Failed to create task');
        }
    } catch (error) {
        console.error('Error creating task:', error);
        showError('Error creating task');
    }
}

async function updateTask(taskId, taskData) {
    try {
        const response = await fetch(`/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData)
        });
        
        if (response.ok) {
            const updatedTask = await response.json();
            const index = tasks.findIndex(t => t.id === taskId);
            if (index !== -1) {
                tasks[index] = updatedTask;
            }
            renderTasks();
            closeModal();
        } else {
            showError('Failed to update task');
        }
    } catch (error) {
        console.error('Error updating task:', error);
        showError('Error updating task');
    }
}

async function deleteTask(taskId) {
    const confirmed = await showConfirm('Are you sure you want to delete this task?');
    if (!confirmed) {
        return;
    }
    
    try {
        const response = await fetch(`/tasks/${taskId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            tasks = tasks.filter(t => t.id !== taskId);
            renderTasks();
        } else {
            showError('Failed to delete task');
        }
    } catch (error) {
        console.error('Error deleting task:', error);
        showError('Error deleting task');
    }
}

// UI Functions
function renderTasks() {
    const filteredTasks = currentFilter === 'all' 
        ? tasks 
        : tasks.filter(task => task.status === currentFilter);
    
    if (filteredTasks.length === 0) {
        tasksGrid.innerHTML = `
            <div class="no-tasks">
                <p>No tasks found</p>
                <p>Click "New Task" to create your first task!</p>
            </div>
        `;
        return;
    }
    
    tasksGrid.innerHTML = filteredTasks.map(task => createTaskCard(task)).join('');
    
    // Add event listeners to task cards
    document.querySelectorAll('.task-card').forEach(card => {
        const taskId = card.dataset.taskId;
        card.addEventListener('click', (e) => {
            if (!e.target.classList.contains('task-btn')) {
                viewTask(taskId);
            }
        });
    });
    
    // Add event listeners to edit and delete buttons
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const taskId = e.target.dataset.taskId;
            openEditModal(taskId);
        });
    });
    
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const taskId = e.target.dataset.taskId;
            deleteTask(taskId);
        });
    });
}

function createTaskCard(task) {
    const shortId = task.id.substring(0, 8);
    const description = task.longDescription 
        ? task.longDescription.substring(0, 100) + (task.longDescription.length > 100 ? '...' : '')
        : 'No description';
    
    return `
        <div class="task-card" data-task-id="${task.id}">
            <div class="task-header">
                <h3 class="task-title">${escapeHtml(task.shortDescription)}</h3>
                <span class="status-badge status-${task.status}">${getStatusDisplayName(task.status)}</span>
            </div>
            <p class="task-description">${escapeHtml(description)}</p>
            <div class="task-meta">
                <span class="task-id">ID: ${shortId}</span>
                <div class="task-actions-buttons">
                    <button class="task-btn edit-btn" data-task-id="${task.id}">Edit</button>
                    <button class="task-btn delete-btn" data-task-id="${task.id}">Delete</button>
                </div>
            </div>
        </div>
    `;
}

function viewTask(taskId) {
    const task = tasks.find(t => t.id === taskId);
    if (!task) return;
    
    document.getElementById('viewTaskId').textContent = task.id;
    document.getElementById('viewShortDescription').textContent = task.shortDescription;
    document.getElementById('viewStatus').textContent = getStatusDisplayName(task.status);
    document.getElementById('viewStatus').className = `status-badge status-${task.status}`;
    document.getElementById('viewLongDescription').textContent = task.longDescription || 'No description';
    document.getElementById('viewCreatedAt').textContent = formatDate(task.createdAt);
    document.getElementById('viewUpdatedAt').textContent = formatDate(task.updatedAt);
    
    document.getElementById('editFromViewBtn').onclick = () => {
        closeViewModal();
        openEditModal(taskId);
    };
    
    viewTaskModal.style.display = 'block';
}

function openCreateModal() {
    editingTaskId = null;
    modalTitle.textContent = 'Create New Task';
    taskForm.reset();
    document.getElementById('status').value = 'TODO';
    taskModal.style.display = 'block';
}

function openEditModal(taskId) {
    const task = tasks.find(t => t.id === taskId);
    if (!task) return;
    
    editingTaskId = taskId;
    modalTitle.textContent = 'Edit Task';
    document.getElementById('taskId').value = task.id;
    document.getElementById('shortDescription').value = task.shortDescription;
    document.getElementById('longDescription').value = task.longDescription || '';
    document.getElementById('status').value = task.status;
    taskModal.style.display = 'block';
}

function closeModal() {
    taskModal.style.display = 'none';
    taskForm.reset();
    editingTaskId = null;
}

function closeViewModal() {
    viewTaskModal.style.display = 'none';
}

function handleFormSubmit(e) {
    e.preventDefault();
    
    const taskData = {
        shortDescription: document.getElementById('shortDescription').value.trim(),
        longDescription: document.getElementById('longDescription').value.trim(),
        status: document.getElementById('status').value
    };
    
    if (editingTaskId) {
        updateTask(editingTaskId, taskData);
    } else {
        createTask(taskData);
    }
}

// Helper Functions
function getStatusDisplayName(status) {
    const statusMap = {
        'TODO': 'To Do',
        'IN_PROGRESS': 'In Progress',
        'DONE': 'Done'
    };
    return statusMap[status] || status;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}


function showError(message) {
    showAlert(message, 'error');
}

function showSuccess(message) {
    showAlert(message, 'success');
}

function showAlert(message, type = 'error') {
    // Remove existing alerts
    const existingAlert = document.querySelector('.custom-alert');
    if (existingAlert) {
        existingAlert.remove();
    }
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `custom-alert custom-alert-${type}`;
    alertDiv.innerHTML = `
        <div class="custom-alert-content">
            <span class="custom-alert-icon">${type === 'error' ? '✕' : '✓'}</span>
            <span class="custom-alert-message">${escapeHtml(message)}</span>
            <button class="custom-alert-close" onclick="this.parentElement.parentElement.remove()">&times;</button>
        </div>
    `;
    
    document.body.appendChild(alertDiv);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (alertDiv.parentElement) {
            alertDiv.remove();
        }
    }, 5000);
}

function showConfirm(message) {
    return new Promise((resolve) => {
        // Remove existing confirms
        const existingConfirm = document.querySelector('.custom-confirm');
        if (existingConfirm) {
            existingConfirm.remove();
        }
        
        const confirmDiv = document.createElement('div');
        confirmDiv.className = 'custom-confirm';
        confirmDiv.innerHTML = `
            <div class="custom-confirm-content">
                <h3>Confirm Action</h3>
                <p>${escapeHtml(message)}</p>
                <div class="custom-confirm-buttons">
                    <button class="confirm-btn confirm-cancel">Cancel</button>
                    <button class="confirm-btn confirm-ok">OK</button>
                </div>
            </div>
        `;
        
        document.body.appendChild(confirmDiv);
        
        const cancelBtn = confirmDiv.querySelector('.confirm-cancel');
        const okBtn = confirmDiv.querySelector('.confirm-ok');
        
        const cleanup = (result) => {
            confirmDiv.remove();
            resolve(result);
        };
        
        cancelBtn.addEventListener('click', () => cleanup(false));
        okBtn.addEventListener('click', () => cleanup(true));
        
        // Close on background click
        confirmDiv.addEventListener('click', (e) => {
            if (e.target === confirmDiv) {
                cleanup(false);
            }
        });
    });
}
