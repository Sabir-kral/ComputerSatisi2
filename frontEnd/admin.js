const API_BASE = "http://95.111.230.66:8080/api";

let token = localStorage.getItem('accessToken');
if (!token) {
    const activeUserStr = localStorage.getItem('activeUser');
    if (activeUserStr) {
        try {
            const activeUser = JSON.parse(activeUserStr);
            token = activeUser.accessToken;
        } catch (e) {
            console.error("Token oxunarkən xəta:", e);
        }
    }
}

if (!token) window.location.href = 'login.html';

let currentUserId = null;
let currentEditingComputerId = null;
let allUsersCache = [];

// --- GİRİŞDƏ ADMİN OLUB-OLMADIĞINI YOXLA ---
(async function verifyAdmin() {
    try {
        const res = await fetch(`${API_BASE}/admin/check`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) {
            alert("Bu səhifəyə giriş icazəniz yoxdur.");
            window.location.href = 'index.html';
        }
    } catch (err) {
        alert("Yoxlama zamanı xəta baş verdi.");
        window.location.href = 'index.html';
    }
})();

function hideAllViews() {
    ['admin-home', 'user-list-view', 'user-detail-view', 'computer-list-view', 'add-admin-view'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = 'none';
    });
}

window.backToHome = function() {
    hideAllViews();
    document.getElementById('admin-home').style.display = 'block';
};

window.backToUserList = function() {
    showUserList();
};

// --- İSTİFADƏÇİ SİYAHISI ---
window.showUserList = async function() {
    hideAllViews();
    document.getElementById('user-list-view').style.display = 'block';

    const bannedContainer = document.getElementById('banned-users-list');
    const allContainer = document.getElementById('all-users-list');
    bannedContainer.innerHTML = "<p>Yüklənir...</p>";
    allContainer.innerHTML = "";

    try {
        const res = await fetch(`${API_BASE}/admin/users`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error();
        const users = await res.json();
        allUsersCache = users;

        const banned = users.filter(u => u.banned);
        const active = users.filter(u => !u.banned);

        bannedContainer.innerHTML = banned.length === 0
            ? "<p style='color:#8b949e;'>Bloklanmış istifadəçi yoxdur.</p>"
            : banned.map(u => renderUserItem(u)).join('');

        allContainer.innerHTML = active.length === 0
            ? "<p style='color:#8b949e;'>İstifadəçi tapılmadı.</p>"
            : active.map(u => renderUserItem(u)).join('');

    } catch (err) {
        bannedContainer.innerHTML = "<p>Xəta baş verdi.</p>";
    }
};

function renderUserItem(u) {
    return `
        <div class="list-item ${u.banned ? 'banned' : ''}" onclick="openUserDetail(${u.id})">
            <div>
                <strong>${u.name} ${u.surname}</strong>
                <div style="color:#8b949e; font-size:0.85rem;">${u.email}</div>
            </div>
            <span class="badge ${u.banned ? 'badge-banned' : 'badge-active'}">${u.banned ? 'Bloklanıb' : 'Aktiv'}</span>
        </div>
    `;
}

// --- İSTİFADƏÇİ DETALI ---
window.openUserDetail = async function(id) {
    currentUserId = id;
    hideAllViews();
    document.getElementById('user-detail-view').style.display = 'block';
    document.getElementById('edit-user-form').style.display = 'none';

    try {
        const res = await fetch(`${API_BASE}/admin/users/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error();
        const user = await res.json();

        document.getElementById('ud-id').innerText = user.id;
        document.getElementById('ud-name').innerText = user.name;
        document.getElementById('ud-surname').innerText = user.surname;
        document.getElementById('ud-email').innerText = user.email;
        document.getElementById('ud-status').innerText = user.banned ? 'Bloklanıb' : 'Aktiv';

        const banBtn = document.getElementById('ban-toggle-btn');
        if (user.banned) {
            banBtn.innerText = 'Blokdan Çıxar';
            banBtn.style.background = '#238636';
        } else {
            banBtn.innerText = 'Banla';
            banBtn.style.background = '#da3633';
        }

        document.getElementById('edit-name').value = user.name;
        document.getElementById('edit-surname').value = user.surname;
        document.getElementById('edit-email').value = user.email;

    } catch (err) {
        alert("İstifadəçi məlumatı yüklənmədi.");
    }
};

window.showEditUser = function() {
    document.getElementById('edit-user-form').style.display = 'block';
};

window.saveUserEdit = async function() {
    const name = document.getElementById('edit-name').value.trim();
    const surname = document.getElementById('edit-surname').value.trim();
    const email = document.getElementById('edit-email').value.trim();

    if (!name || !surname || !email) {
        alert("Bütün sahələr doldurulmalıdır!");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/admin/users/${currentUserId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, surname, email })
        });

        if (res.ok) {
            alert("İstifadəçi yeniləndi!");
            openUserDetail(currentUserId);
        } else {
            alert("Xəta baş verdi (Status: " + res.status + ")");
        }
    } catch (err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

window.toggleBan = async function() {
    const banBtn = document.getElementById('ban-toggle-btn');
    const isBanned = banBtn.innerText === 'Blokdan Çıxar';
    const endpoint = isBanned ? 'unban' : 'ban';

    if (!confirm(isBanned ? "Bu istifadəçinin blokunu qaldırmaq istəyirsiniz?" : "Bu istifadəçini bloklamaq istəyirsiniz?")) return;

    try {
        const res = await fetch(`${API_BASE}/admin/users/${currentUserId}/${endpoint}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            openUserDetail(currentUserId);
        } else {
            alert("Xəta baş verdi.");
        }
    } catch (err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

// --- KOMPÜTER SİYAHISI ---
window.showComputerList = async function() {
    hideAllViews();
    document.getElementById('computer-list-view').style.display = 'block';

    const container = document.getElementById('admin-computer-list');
    container.innerHTML = "<p>Yüklənir...</p>";

    try {
        const res = await fetch(`${API_BASE}/admin/computers`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error();
        const computers = await res.json();

        if (!computers || computers.length === 0) {
            container.innerHTML = "<p style='color:#8b949e;'>Kompüter tapılmadı.</p>";
            return;
        }

        container.innerHTML = computers.map(pc => `
            <div class="list-item" style="cursor:default;">
                <div>
                    <strong>${pc.name}</strong>
                    <div style="color:#49fb35;">${pc.price} AZN</div>
                </div>
                <div style="display:flex; gap:8px;">
                    <button class="action-btn" style="background:#58a6ff; color:#000;" onclick='openEditComputer(${pc.id}, ${JSON.stringify(pc.name)}, ${pc.price}, ${JSON.stringify(pc.description || "")})'>Redaktə</button>
                    <button class="action-btn" style="background:#da3633; color:white;" onclick="deleteComputer(${pc.id})">Sil</button>
                </div>
            </div>
        `).join('');

    } catch (err) {
        container.innerHTML = "<p>Xəta baş verdi.</p>";
    }
};

window.openEditComputer = function(id, name, price, description) {
    currentEditingComputerId = id;
    document.getElementById('update-pc-name').value = name;
    document.getElementById('update-pc-price').value = price;
    document.getElementById('update-pc-desc').value = description;
    document.getElementById('updateComputerModal').style.display = 'flex';
};

window.submitUpdateComputer = async function() {
    if (!currentEditingComputerId) return;

    const name = document.getElementById('update-pc-name').value.trim();
    const price = parseFloat(document.getElementById('update-pc-price').value);
    const description = document.getElementById('update-pc-desc').value.trim();

    if (!name || isNaN(price)) {
        alert("Ad və Qiymət düzgün doldurulmalıdır!");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/computers/${currentEditingComputerId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, price, description })
        });

        if (res.ok) {
            alert("Kompüter yeniləndi!");
            document.getElementById('updateComputerModal').style.display = 'none';
            showComputerList();
        } else {
            alert("Xəta baş verdi (Status: " + res.status + ")");
        }
    } catch (err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

window.deleteComputer = async function(id) {
    if (!confirm("Bu kompüteri silmək istədiyinizdən əminsiniz?")) return;
    try {
        const res = await fetch(`${API_BASE}/computers/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            showComputerList();
        } else {
            alert("Silinmə zamanı xəta oldu.");
        }
    } catch (err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

// --- ADMIN TƏYİN ET ---
window.showAddAdmin = async function() {
    hideAllViews();
    document.getElementById('add-admin-view').style.display = 'block';

    const select = document.getElementById('admin-user-select');
    select.innerHTML = "<option value=''>-- Yüklənir... --</option>";

    try {
        const res = await fetch(`${API_BASE}/admin/users`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error();
        const users = await res.json();

        select.innerHTML = "<option value=''>-- İstifadəçi seç --</option>" +
            users.map(u => `<option value="${u.email}">${u.name} ${u.surname} (${u.email})</option>`).join('');

    } catch (err) {
        select.innerHTML = "<option value=''>Yüklənmədi</option>";
    }
};

window.promoteSelected = async function() {
    const email = document.getElementById('admin-user-select').value;
    if (!email) {
        alert("Zəhmət olmasa istifadəçi seçin!");
        return;
    }
    await promoteEmail(email);
};

window.promoteCustomEmail = async function() {
    const email = document.getElementById('admin-custom-email').value.trim();
    if (!email) {
        alert("Email yazın!");
        return;
    }
    await promoteEmail(email);
};

async function promoteEmail(email) {
    if (!confirm(`${email} email-ini admin etmək istədiyinizdən əminsiniz?`)) return;
    try {
        const res = await fetch(`${API_BASE}/admin/promote?email=${encodeURIComponent(email)}`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            alert("İstifadəçi admin edildi!");
            document.getElementById('admin-custom-email').value = '';
        } else {
            alert("Xəta baş verdi (Status: " + res.status + ")");
        }
    } catch (err) {
        alert("Şəbəkə xətası baş verdi.");
    }
}