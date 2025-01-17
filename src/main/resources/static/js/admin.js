let currentUserId = null;
const addRoleModal = document.getElementById('addRoleModal');
const deleteModal = document.getElementById('deleteModal');
const toast = document.getElementById('toast');

document.addEventListener('DOMContentLoaded', function() {
    const message = sessionStorage.getItem('toastMessage');
    const type = sessionStorage.getItem('toastType');

    if (message) {
        setTimeout(() => {
            showToast(message, type);
            sessionStorage.removeItem('toastMessage');
            sessionStorage.removeItem('toastType');
        }, 500); 
    }
});

document.addEventListener('click', function(e) {
    if (e.target.classList.contains('role-add-btn')) {
        currentUserId = e.target.dataset.userId;
        addRoleModal.style.display = 'block';
    }

    if (e.target.classList.contains('role-remove-btn')) {
        const roleBadge = e.target.closest('.role-badge');
        const userId = roleBadge.dataset.userId;
        const role = roleBadge.dataset.role;
        if (confirm('Are you sure you want to remove this role?')) {
            removeRole(userId, role, roleBadge);
        }
    }

    if (e.target.classList.contains('toggle-btn')) {
        const userId = e.target.dataset.userId;
        const currentStatus = parseInt(e.target.dataset.status);
        toggleStatus(userId, currentStatus);
    }

    if (e.target.classList.contains('delete-btn') && !e.target.closest('.modal')) {
        currentUserId = e.target.dataset.userId;
        deleteModal.style.display = 'block';
    }

    if (e.target.classList.contains('close') ||
        e.target.classList.contains('cancel-btn')) {
        closeAllModals();
    }
});

addRoleModal.querySelector('.save-btn').addEventListener('click', function() {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;
    const role = document.getElementById('newRoleSelect').value;

    fetch(`/admin/users/${currentUserId}/roles/add?role=${role}`, {
        method: 'POST',
        headers: {
            [header]: token
        }
    })
    .then(response => {
        if (response.ok) {
            sessionStorage.setItem('toastMessage', 'Role added successfully');
            sessionStorage.setItem('toastType', 'success');
            closeAllModals();
            window.location.reload();
        } else {
            showToast('Error adding role', 'error');
        }
    })
    .catch(error => {
        showToast('Error adding role', 'error');
        console.error('Error:', error);
    });
});

function removeRole(userId, role, roleBadge) {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch(`/admin/users/${userId}/roles/${role}`, {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    })
    .then(response => {
        if (response.ok) {
            roleBadge.remove();
            showToast('Role removed successfully', 'success');
        } else {
            showToast('Error removing role', 'error');
        }
    })
    .catch(error => {
        showToast('Error removing role', 'error');
        console.error('Error:', error);
    });
}

function toggleStatus(userId, currentStatus) {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;
    const newStatus = currentStatus === 1 ? 0 : 1;

    fetch(`/admin/users/${userId}/status?enabled=${newStatus}`, {
        method: 'POST',
        headers: {
            [header]: token
        }
    })
    .then(response => {
        if (response.ok) {
            sessionStorage.setItem('toastMessage', 'Status updated successfully');
            sessionStorage.setItem('toastType', 'success');
            closeAllModals();
            window.location.reload();
        } else {
            showToast('Error updating status', 'error');
        }
    })
    .catch(error => {
        showToast('Error updating status', 'error');
        console.error('Error:', error);
    });
}

deleteModal.querySelector('.delete-btn').addEventListener('click', function() {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch(`/admin/users/${currentUserId}`, {
        method: 'DELETE',
        headers: {
            [header]: token
        }
    })
    .then(response => {
        if (response.ok) {
            sessionStorage.setItem('toastMessage', 'User deleted successfully');
            sessionStorage.setItem('toastType', 'success');
            closeAllModals();
            window.location.reload();
        } else {
            showToast('Error deleting user', 'error');
        }

        
    })
    .catch(error => {
        showToast('Error deleting user', 'error');
        console.error('Error:', error);
    });
});

function closeAllModals() {
    addRoleModal.style.display = 'none';
    deleteModal.style.display = 'none';
}

function showToast(message, type) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type}`;

    toast.style.display = 'block';
    toast.style.opacity = '1';

    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => {
            toast.style.display = 'none';
        }, 300);
    }, 1000);
}

window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        closeAllModals();
    }
}