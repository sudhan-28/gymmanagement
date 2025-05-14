// Base URL for API
const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to make API calls
async function callApi(endpoint, method, data = null) {
    try {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (data) {
            options.body = JSON.stringify(data);
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Request failed with status ${response.status}: ${errorText}`);
        }

        // For DELETE requests that might not return JSON
        if (method === 'DELETE') {
            return await response.text();
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Show notification to user
function showNotification(message, isError = false) {
    alert(isError ? `Error: ${message}` : message);
}

// Handle user registration form submission
document.getElementById('registerForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    try {
        const userData = {
            name: this.querySelector('input[name="name"]').value,
            email: this.querySelector('input[name="email"]').value,
            password: this.querySelector('input[name="password"]').value,
            role: this.querySelector('input[name="role"]').value,
            phone: this.querySelector('input[name="phone"]').value
        };

        const result = await callApi('/user/register', 'POST', userData);

        showNotification(`User registered successfully with ID: ${result.id}`);
        this.reset();
    } catch (error) {
        console.error('Registration error:', error);
        showNotification(error.message, true);
    }
});

// Handle user login form submission
document.getElementById('loginForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    try {
        const loginData = {
            email: this.querySelector('input[name="email"]').value,
            password: this.querySelector('input[name="password"]').value
        };

        const result = await callApi('/user/login', 'POST', loginData);

        if (result && result.id) {
            sessionStorage.setItem('currentUser', JSON.stringify(result));
            displayUserDetails(result);
            showNotification(`Login successful! Welcome ${result.name}`);
        } else {
            showNotification('Invalid email or password', true);
        }
    } catch (error) {
        console.error('Login error:', error);
        showNotification('Login failed. Please check your credentials.', true);
    }
});

// Display logged-in user details
function displayUserDetails(user) {
    document.getElementById('registerSection').style.display = 'none';
    document.getElementById('loginSection').style.display = 'none';
    document.getElementById('userDetailsSection').style.display = 'block';

    document.getElementById('userName').textContent = `Name: ${user.name}`;
    document.getElementById('userEmail').textContent = `Email: ${user.email}`;
    document.getElementById('userPhone').textContent = `Phone: ${user.phone}`;

    // Show update and delete options
    document.getElementById('updateBtn').onclick = () => showUpdateForm(user);
    document.getElementById('deleteBtn').onclick = () => deleteUser(user.id);
}

// Show update form with user data
function showUpdateForm(user) {
    document.getElementById('updateFormSection').style.display = 'block';
    document.getElementById('updateForm').elements['id'].value = user.id;
    document.getElementById('updateForm').elements['name'].value = user.name;
    document.getElementById('updateForm').elements['email'].value = user.email;
    document.getElementById('updateForm').elements['phone'].value = user.phone;
}

// Handle user update form submission
document.getElementById('updateForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();

    const userId = this.querySelector('input[name="id"]').value;
    const updatedData = {
        name: this.querySelector('input[name="name"]').value,
        email: this.querySelector('input[name="email"]').value,
        phone: this.querySelector('input[name="phone"]').value
    };

    try {
        const result = await callApi(`/user/update/${userId}`, 'PUT', updatedData);
        showNotification('User updated successfully!');
        this.reset();
        displayUserDetails(result); // Update user info
    } catch (error) {
        console.error('Update error:', error);
        showNotification(error.message, true);
    }
});

// Handle user delete request
async function deleteUser(userId) {
    if (confirm(`Are you sure you want to delete user with ID: ${userId}?`)) {
        try {
            await callApi(`/user/delete/${userId}`, 'DELETE');
            showNotification('User deleted successfully!');
            sessionStorage.removeItem('currentUser');
            // Optionally, redirect or reset the page to login
            location.reload();
        } catch (error) {
            console.error('Delete error:', error);
            showNotification(error.message, true);
        }
    }
}
