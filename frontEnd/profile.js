const token = localStorage.getItem('accessToken');
if (!token) location.href = 'login.html';

let currentComputerId = null;

async function loadProfile() {
    const response = await fetch('https://localhost:8080/api/customers/profile', {
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
    document.getElementById('my-computers-area').style.display = 'none';
    document.getElementById('selling-area').style.display = 'none';
}

async function processUpdate() {
    const currentEmail = localStorage.getItem('userEmail');
    const data = {
        name: document.getElementById('up-name').value,
        surname: document.getElementById('up-surname').value,
        email: currentEmail,
        password: document.getElementById('up-pass').value
    };

    try {
        const response = await fetch(`https://localhost:8080/api/customers/profile?email=${encodeURIComponent(currentEmail)}`, {
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
        await fetch('https://localhost:8080/api/customers/delete', {
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
    // 1. Summary sahəsini JS-ə tanıt
    const summaryArea = document.getElementById("checkout-summary"); 

    document.getElementById("selling-area").style.display = "none";
    document.getElementById("update-form").style.display = "none";

    area.style.display = "block";
    container.innerHTML = "<p style='color:white'>Yüklənir...</p>";

    try {
        const res = await fetch('https://localhost:8080/api/customers/v1', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            const computers = await res.json();

            if (computers.length === 0) {
                container.innerHTML = "<p style='color:#8b949e;'>Siz hələ heç bir kompüter almamısınız.</p>";
                // 2. Əgər kompüter yoxdursa, summary sahəsini gizlət
                if(summaryArea) summaryArea.style.display = "none"; 
                return;
            }

            // 3. Əgər kompüter VARSA, summary sahəsini GÖSTƏR
            if(summaryArea) summaryArea.style.display = "block";

            let totalPrice = 0; // Cəmi hesablamaq üçün

            container.innerHTML = computers.map(pc => {
                totalPrice += pc.price; // Qiymətləri topla
                return `
                <div style="background:#0d1117; border:1px solid #30363d; border-radius:10px; padding:20px; margin-bottom:15px;">
                    <h3 style="color:#58a6ff; margin:0 0 8px 0;">${pc.name}</h3>
                    <p style="margin:0 0 5px 0;">${pc.description}</p>
                    <p style="color:#58a6ff; font-weight:bold; margin:0;">${pc.price} AZN</p>
                </div>
                `;
            }).join('');

            // 4. Ümumi cəmi ekrana yazdır
            const totalDisplay = document.getElementById('total-price-display');
            if(totalDisplay) totalDisplay.innerText = totalPrice;

            area.scrollIntoView({ behavior: 'smooth' });
        } else {
            container.innerHTML = "<p style='color:red;'>Məlumatlar gətirilərkən xəta baş verdi.</p>";
            if(summaryArea) summaryArea.style.display = "none";
        }
    } catch (err) {
        container.innerHTML = "<p style='color:red;'>Serverlə bağlantı kəsildi.</p>";
        if(summaryArea) summaryArea.style.display = "none";
    }
}

function goToCheckout(){
    window.href = "checkout.html"
}

async function getSellingComputers() {
    const area = document.getElementById("selling-area");
    const container = document.getElementById("selling-list");

    document.getElementById("my-computers-area").style.display = "none";
    document.getElementById("update-form").style.display = "none";

    area.style.display = "block";
    container.innerHTML = "<p style='color:white'>Yüklənir...</p>";

    try {
        const res = await fetch('https://localhost:8080/api/customers/selling', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            const computers = await res.json();

            if (computers.length === 0) {
                container.innerHTML = "<p style='color:#8b949e;'>Siz hələ heç bir kompüter satmırsınız.</p>";
                return;
            }

            container.innerHTML = computers.map(pc => `
                <div onclick="openSellingDetail(${pc.id})" style="
                    background:#0d1117; border:1px solid #30363d;
                    border-radius:10px; padding:20px; margin-bottom:15px;
                    cursor:pointer; transition:0.3s;"
                    onmouseover="this.style.borderColor='#58a6ff'"
                    onmouseout="this.style.borderColor='#30363d'">
                    <h3 style="color:#58a6ff; margin:0 0 8px 0;">${pc.name}</h3>
                    <p style="margin:0 0 5px 0;">${pc.description}</p>
                    <p style="color:#58a6ff; font-weight:bold; margin:0;">${pc.price} AZN</p>
                </div>
            `).join('');

            area.scrollIntoView({ behavior: 'smooth' });
        } else {
            container.innerHTML = "<p style='color:red;'>Xəta baş verdi.</p>";
        }
    } catch (err) {
        container.innerHTML = "<p style='color:red;'>Bağlantı xətası!</p>";
    }
}

async function openSellingDetail(id) {
    const overlay = document.getElementById("sellingOverlay");
    const content = document.getElementById("sellingDetailContent");

    currentComputerId = id;
    overlay.style.display = "flex";
    content.innerHTML = "<p style='color:white;text-align:center;'>Yüklənir...</p>";

    try {
        const res = await fetch(`https://localhost:8080/api/computers/${id}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const pc = await res.json();
        const img = pc.imageLinks?.[0] || 'https://via.placeholder.com/400x250?text=No+Image';

        content.innerHTML = `
            <img src="${img}" style="width:100%;border-radius:12px;border:3px solid #58a6ff;margin-bottom:20px;"
                onerror="this.src='https://via.placeholder.com/400x250?text=No+Image'">
            <h2 style="color:white;margin:0 0 10px 0;">${pc.name}</h2>
            <p style="color:#c9d1d9;margin:0 0 10px 0;">${pc.description}</p>
            <p style="color:#58a6ff;font-size:1.8rem;font-weight:bold;margin:0 0 25px 0;">${pc.price} AZN</p>
            <div style="display:flex;gap:12px;">
                <button onclick="openUpdateModal(${pc.id}, '${pc.name.replace(/'/g, "\\'")}', ${pc.price}, '${pc.description.replace(/'/g, "\\'")}')"
                    style="flex:1;padding:12px;background:#58a6ff;border:none;border-radius:8px;color:#000;font-weight:bold;cursor:pointer;font-size:1rem;">
                    ✏️ Yenilə
                </button>
                <button onclick="deleteComputer(${pc.id})"
                    style="flex:1;padding:12px;background:transparent;border:2px solid #f87171;border-radius:8px;color:#f87171;font-weight:bold;cursor:pointer;font-size:1rem;">
                    🗑️ Sil
                </button>
                <button onclick="closeSellingDetail()"
                    style="flex:1;padding:12px;background:transparent;border:1px solid #30363d;border-radius:8px;color:#c9d1d9;font-weight:bold;cursor:pointer;font-size:1rem;">
                    ← Geri
                </button>
            </div>
        `;
    } catch (e) {
        closeSellingDetail();
    }
}

function closeSellingDetail() {
    document.getElementById("sellingOverlay").style.display = "none";
}

function openUpdateModal(id, name, price, description) {
    currentComputerId = id;
    document.getElementById("update-pc-name").value = name;
    document.getElementById("update-pc-price").value = price;
    document.getElementById("update-pc-desc").value = description;
    document.getElementById("updateComputerModal").style.display = "flex";
}

function closeUpdateModal() {
    document.getElementById("updateComputerModal").style.display = "none";
}

async function submitUpdateComputer() {
    const name = document.getElementById("update-pc-name").value;
    const price = document.getElementById("update-pc-price").value;
    const description = document.getElementById("update-pc-desc").value;

    if (!name || !price || !description) {
        alert("Bütün xanaları doldurun!");
        return;
    }

    try {
        const res = await fetch(`https://localhost:8080/api/computers/${currentComputerId}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, price: parseFloat(price), description })
        });

        if (res.ok) {
            alert("Kompüter uğurla yeniləndi!");
            closeUpdateModal();
            closeSellingDetail();
            getSellingComputers();
        } else {
            alert("Yeniləmə uğursuz oldu!");
        }
    } catch (err) {
        alert("Bağlantı xətası!");
    }
}

async function deleteComputer(id) {
    if (!confirm("Bu kompüteri silmək istədiyinizdən əminsiniz?")) return;

    try {
        const res = await fetch(`https://localhost:8080/api/computers/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            alert("Kompüter uğurla silindi!");
            closeSellingDetail();
            getSellingComputers();
        } else {
            alert("Silmə uğursuz oldu!");
        }
    } catch (err) {
        alert("Bağlantı xətası!");
    }
}

loadProfile();