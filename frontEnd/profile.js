const token = localStorage.getItem('accessToken');
if (!token) location.href = 'login.html';

async function loadProfile() {
    const response = await fetch('http://localhost:8080/api/customers/profile', {
        headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.status === 401 || response.status === 403) {
        localStorage.clear();
        location.href = 'login.html';
        return;
    }

    const user = await response.json();
    document.getElementById('view-name').innerText = user.name;
    document.getElementById('view-surname').innerText = user.surname;
    document.getElementById('view-email').innerText = user.email;
}

function showUpdate() {
    document.getElementById('profile-view').style.display = 'none';
    document.getElementById('update-form').style.display = 'block';
}

async function processUpdate() {
    const token = localStorage.getItem('accessToken');
    const currentEmail = localStorage.getItem('userEmail');

    const data = {
        name: document.getElementById('up-name').value,
        surname: document.getElementById('up-surname').value,
        email: currentEmail, // email changes need special handling; keep current for now
        password: document.getElementById('up-pass').value
    };

    try {
        // Backend: PUT /api/customers/profile?email=xxx
        const response = await fetch(`http://localhost:8080/api/customers/profile?email=${encodeURIComponent(currentEmail)}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert("Məlumatlar uğurla yeniləndi!");
            location.reload();
        } else if (response.status === 403) {
            alert("Giriş icazəniz yoxdur.");
        } else {
            const err = await response.json();
            alert("Xəta: " + (err.message || "Yeniləmə uğursuz oldu."));
        }
    } catch (err) {
        console.error("Update xətası:", err);
        alert("Bağlantı xətası!");
    }
}

async function deleteAccount() {
    if (confirm("Hesabınız həmişəlik silinsin?")) {
        await fetch('http://localhost:8080/api/customers/delete', {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        logout();
    }
}

function logout() {
    localStorage.clear();
    location.href = 'index.html';
}

async function getMyComputers() {
    const area = document.getElementById("my-computers-area");
    const container = document.getElementById("pc-list-content");

    area.style.display = "block";
    container.innerHTML = "<p style='color:white'>Yüklənir...</p>";

    try {
        const res = await fetch('http://localhost:8080/api/customers/v1', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            const computers = await res.json();
            
            if (computers.length === 0) {
                container.innerHTML = "<p>Sizin hələ ki, heç bir kompüteriniz yoxdur.</p>";
                return;
            }

            container.innerHTML = computers.map(pc => `
                <div class="pc-list-item">
                    <h4>${pc.name}</h4>
                    <p>${pc.description}</p>
                    <p class="pc-price">${pc.price} AZN</p>
                </div>
            `).join('');

            area.scrollIntoView({ behavior: 'smooth' });
        } else {
            container.innerHTML = "<p style='color:red'>Məlumatlar gətirilərkən xəta baş verdi.</p>";
        }
    } catch (err) {
        container.innerHTML = "<p style='color:red'>Serverlə bağlantı kəsildi.</p>";
    }
}
async function getSellingComputers() {
    const token = localStorage.getItem('accessToken');
    const area = document.getElementById("selling-area");
    const container = document.getElementById("selling-list");

    area.style.display = "block";
    container.innerHTML = "<p style='color:white'>Yüklənir...</p>";

    try {
        const res = await fetch('http://localhost:8080/api/customers/selling', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            const computers = await res.json();

            if (computers.length === 0) {
                container.innerHTML = "<p style='color:#8b949e;'>Siz hələ heç bir kompüter satmırsınız.</p>";
                return;
            }

            container.innerHTML = computers.map(pc => `
                <div style="background:#161b22; border:1px solid #30363d; border-radius:10px; padding:20px; margin-bottom:15px;">
                    <h3 style="color:#58a6ff; margin:0 0 8px 0;">${pc.name}</h3>
                    <p style="margin:0 0 5px 0;">${pc.description}</p>
                    <p style="color:#58a6ff; font-weight:bold; margin:0;">${pc.price} AZN</p>
                </div>
            `).join('');
        } else {
            container.innerHTML = "<p style='color:red;'>Məlumatlar gətirilərkən xəta baş verdi.</p>";
        }
    } catch (err) {
        container.innerHTML = "<p style='color:red;'>Bağlantı xətası!</p>";
    }
}

loadProfile();