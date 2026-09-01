const API_BASE = "http://localhost:8080/api/admin"; // Server ünvanına uyğun dəyişə bilərsən

// Token-i localStorage-dan götürürük (giriş edərkən saxlanılan token)
function getAuthHeaders() {
    const token = localStorage.getItem("token") || localStorage.getItem("jwt");
    return {
        "Content-Type": "application/json",
        "Authorization": token ? `Bearer ${token}` : ""
    };
}

// Səhifə açıldıqda yoxlama və məlumatların çəkilməsi
document.addEventListener("DOMContentLoaded", async () => {
    try {
        const res = await fetch(`${API_BASE}/check`, {
            headers: getAuthHeaders()
        });
        if (!res.ok) {
            alert("Bu səhifəyə giriş icazəniz yoxdur və ya admin deyilsiniz!");
            window.location.href = "index.html";
            return;
        }
    } catch (e) {
        alert("Serverə qoşulmaq mümkün olmadı.");
        window.location.href = "index.html";
        return;
    }

    loadUsers();
    loadComputers();
});

// Tab dəyişmə funksiyası
function switchTab(tabName) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    if (tabName === 'users') {
        document.querySelector('.tabs button:nth-child(1)').classList.add('active');
        document.getElementById('users-tab').classList.add('active');
        loadUsers();
    } else if (tabName === 'computers') {
        document.querySelector('.tabs button:nth-child(2)').classList.add('active');
        document.getElementById('computers-tab').classList.add('active');
        loadComputers();
    } else if (tabName === 'admins') {
        document.querySelector('.tabs button:nth-child(3)').classList.add('active');
        document.getElementById('admins-tab').classList.add('active');
    }
}

// İstifadəçiləri yüklə
async function loadUsers() {
    try {
        const res = await fetch(`${API_BASE}/users`, { headers: getAuthHeaders() });
        const users = await res.json();
        const tbody = document.getElementById('users-table-body');
        tbody.innerHTML = '';

        users.forEach(u => {
            tbody.innerHTML += `
                <tr>
                    <td>${u.id}</td>
                    <td>${u.name || ''}</td>
                    <td>${u.surname || ''}</td>
                    <td>${u.email}</td>
                    <td><span class="badge ${u.banned ? 'banned' : 'active'}">${u.banned ? 'Bloklanıb' : 'Aktiv'}</span></td>
                    <td>
                        <div class="actions">
                            <button class="action-btn btn-edit" onclick="openEditModal(${u.id}, '${u.name || ''}', '${u.surname || ''}', '${u.email}')">Redaktə</button>
                            ${u.banned 
                                ? `<button class="action-btn btn-unban" onclick="unbanUser(${u.id})">Bloku aç</button>`
                                : `<button class="action-btn btn-ban" onclick="banUser(${u.id})">Blokla</button>`
                            }
                        </div>
                    </td>
                </tr>
            `;
        });
    } catch (e) {
        console.error("İstifadəçiləri yükləmək mümkün olmadı", e);
    }
}

// Kompüterləri yüklə
async function loadComputers() {
    try {
        const res = await fetch(`${API_BASE}/computers`, { headers: getAuthHeaders() });
        const computers = await res.json();
        const tbody = document.getElementById('computers-table-body');
        tbody.innerHTML = '';

        computers.forEach(c => {
            tbody.innerHTML += `
                <tr>
                    <td>${c.id}</td>
                    <td>${c.brand || c.name || '-'}</td>
                    <td>${c.model || '-'}</td>
                    <td>${c.price || 0} AZN</td>
                </tr>
            `;
        });
    } catch (e) {
        console.error("Kompüterləri yükləmək mümkün olmadı", e);
    }
}

// İstifadəçini blokla
async function banUser(id) {
    if (!confirm("Bu istifadəçini bloklamaq istədiyinizə əminsinizmi?")) return;
    try {
        const res = await fetch(`${API_BASE}/users/${id}/ban`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await res.json();
        alert(data.message);
        loadUsers();
    } catch (e) {
        alert("Xəta baş verdi");
    }
}

// Blokunu aç
async function unbanUser(id) {
    try {
        const res = await fetch(`${API_BASE}/users/${id}/unban`, {
            method: 'PUT',
            headers: getAuthHeaders()
        });
        const data = await res.json();
        alert(data.message);
        loadUsers();
    } catch (e) {
        alert("Xəta baş verdi");
    }
}

// Modal pəncərəni aç
function openEditModal(id, name, surname, email) {
    document.getElementById('edit-id').value = id;
    document.getElementById('edit-name').value = name;
    document.getElementById('edit-surname').value = surname;
    document.getElementById('edit-email').value = email;
    document.getElementById('edit-modal').classList.add('open');
}

// Modalı bağla
function closeModal() {
    document.getElementById('edit-modal').classList.remove('open');
}

// İstifadəçi məlumatlarını yenilə
async function saveUser() {
    const id = document.getElementById('edit-id').value;
    const body = {
        name: document.getElementById('edit-name').value,
        surname: document.getElementById('edit-surname').value,
        email: document.getElementById('edit-email').value
    };

    try {
        const res = await fetch(`${API_BASE}/users/${id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(body)
        });
        const data = await res.json();
        alert(data.message);
        closeModal();
        loadUsers();
    } catch (e) {
        alert("Yenilənmə zamanı xəta baş verdi");
    }
}

// İstifadəçini admin et
async function promoteUser() {
    const email = document.getElementById('promote-email').value;
    if (!email) {
        alert("Zəhmət olmasa email daxil edin");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/promote?email=${encodeURIComponent(email)}`, {
            method: 'POST',
            headers: getAuthHeaders()
        });
        const data = await res.json();
        alert(data.message);
        document.getElementById('promote-email').value = '';
    } catch (e) {
        alert("Admin təyin edilərkən xəta baş verdi");
    }
}