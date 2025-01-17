let currentUserId = null;
let currentProductId = null;
const addRoleModal = document.getElementById('addRoleModal');
const deleteModal = document.getElementById('deleteModal');
const productModal = document.getElementById('productModal');
const deleteProductModal = document.getElementById('deleteProductModal');
const toast = document.getElementById('toast');
const productSection = document.getElementById('products-section');
const userSection = document.getElementById('users-section');

function reloadPage() {
    const currentSection = sessionStorage.getItem('activeSection');
    if (currentSection) {
        sessionStorage.setItem('reloadSection', currentSection);
    }
    window.location.reload();
}

document.addEventListener('DOMContentLoaded', function() {
    // Toast message handling
    const message = sessionStorage.getItem('toastMessage');
    const type = sessionStorage.getItem('toastType');

    if (message) {
        setTimeout(() => {
            showToast(message, type);
            sessionStorage.removeItem('toastMessage');
            sessionStorage.removeItem('toastType');
        }, 500);
    }

    // Navigation handling with session storage
    const activeSection = sessionStorage.getItem('activeSection') || 'users';

    // Set initial section display based on stored value
    if (activeSection === 'products') {
        productSection.style.display = 'block';
        userSection.style.display = 'none';
    } else {
        productSection.style.display = 'none';
        userSection.style.display = 'block';
    }

    // Set initial active button
    document.querySelectorAll('.nav-btn').forEach(button => {
        if (button.dataset.section === activeSection) {
            button.classList.add('active');
        }

        button.addEventListener('click', () => {
            document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');

            if (button.dataset.section === 'products') {
                productSection.style.display = 'block';
                userSection.style.display = 'none';
                sessionStorage.setItem('activeSection', 'products');
            } else {
                productSection.style.display = 'none';
                userSection.style.display = 'block';
                sessionStorage.setItem('activeSection', 'users');
            }
        });
    });

    // Product management
    const addProductBtn = document.getElementById('add-product-btn');
    const productForm = document.getElementById('product-form');

    // Add new product button handler
    addProductBtn?.addEventListener('click', () => {
        currentProductId = null;
        productForm.reset();
        document.getElementById('modal-title').textContent = 'Add Product';
        productModal.style.display = 'block';
    });

    // Product form submission
    productForm?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        const formData = {
            name: document.getElementById('productName').value,
            price: parseFloat(document.getElementById('productPrice').value),
            image: document.getElementById('productImage').value || null
        };

        const url = currentProductId ? `/products/${currentProductId}` : '/products';
        const method = currentProductId ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                showToast('Product saved successfully', 'success');
                productModal.style.display = 'none';
                reloadPage();
            } else {
                showToast('Error saving product', 'error');
            }
        } catch (error) {
            showToast('Error saving product', 'error');
            console.error('Error:', error);
        }
    });
});

// Add Role Modal Event Handler
addRoleModal?.querySelector('.save-btn')?.addEventListener('click', function() {
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
            reloadPage();
        } else {
            showToast('Error adding role', 'error');
        }
    })
    .catch(error => {
        showToast('Error adding role', 'error');
        console.error('Error:', error);
    });
});

// Click event delegation for both users and products
document.addEventListener('click', async function(e) {
    // User management click handlers
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

    // Product management click handlers
    if (e.target.classList.contains('edit-btn')) {
        currentProductId = e.target.dataset.productId;
        try {
            const response = await fetch(`/products/${currentProductId}`);
            if (response.ok) {
                const product = await response.json();
                document.getElementById('productName').value = product.name;
                document.getElementById('productPrice').value = product.price;
                document.getElementById('productImage').value = product.image || '';
                document.getElementById('modal-title').textContent = 'Edit Product';
                productModal.style.display = 'block';
            }
        } catch (error) {
            showToast('Error loading product', 'error');
            console.error('Error:', error);
        }
    }

    // Delete handlers for both users and products
    if (e.target.classList.contains('delete-btn') && !e.target.closest('.modal')) {
        if (e.target.dataset.productId) {
            currentProductId = e.target.dataset.productId;
            deleteProductModal.style.display = 'block';
        } else if (e.target.dataset.userId) {
            currentUserId = e.target.dataset.userId;
            deleteModal.style.display = 'block';
        }
    }

    // Modal close handlers
    if (e.target.classList.contains('close') || e.target.classList.contains('cancel-btn')) {
        closeAllModals();
    }
});

// Remove Role Function
function removeRole(userId, roleName, roleBadge) {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch(`/admin/users/${userId}/roles/${roleName}`, {
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

// Toggle Status Function
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
            reloadPage();
        } else {
            showToast('Error updating status', 'error');
        }
    })
    .catch(error => {
        showToast('Error updating status', 'error');
        console.error('Error:', error);
    });
}

// Delete User Event Handler
deleteModal?.querySelector('.delete-btn')?.addEventListener('click', function() {
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
            reloadPage();
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
    productModal.style.display = 'none';
    deleteProductModal.style.display = 'none';
}

// Delete product handler
deleteProductModal?.querySelector('.delete-btn')?.addEventListener('click', async function() {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    try {
        const response = await fetch(`/products/${currentProductId}`, {
            method: 'DELETE',
            headers: {
                [header]: token
            }
        });

        if (response.ok) {
            showToast('Product deleted successfully', 'success');
            deleteProductModal.style.display = 'none';
            reloadPage();
        } else {
            showToast('Error deleting product', 'error');
        }
    } catch (error) {
        showToast('Error deleting product', 'error');
        console.error('Error:', error);
    }
});

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
};