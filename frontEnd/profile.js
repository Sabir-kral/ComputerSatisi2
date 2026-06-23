// Tokeni əldə et
let token = localStorage.getItem('accessToken');
if (!token) {
    const activeUserStr = localStorage.getItem('activeUser');
    if (activeUserStr) {
        try {
            const activeUser = JSON.parse(activeUserStr);
            token = activeUser.accessToken;
        } catch(e) { console.error(e); }
    }
}
if (!token) window.location.href = 'login.html';

let currentCustomerEmail = "";

document.addEventListener('DOMContentLoaded', loadProfile);

// --- PROFİLİ YÜKLƏ ---
async function loadProfile() {
    try {
        const response = await fetch('http://95.111.230.66:8080/api/customers/profile', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            const user = await response.json();
            document.getElementById('view-name').innerText = user.name;
            document.getElementById('view-surname').innerText = user.surname;
            document.getElementById('view-email').innerText = user.email;
            currentCustomerEmail = user.email;
            if(document.getElementById('up-name')) document.getElementById('up-name').value = user.name;
            if(document.getElementById('up-surname')) document.getElementById('up-surname').value = user.surname;
        }
    } catch (err) { console.error(err); }
}

// --- REDAKTƏ ET (PUT /api/customers/profile) ---
window.processUpdate = async function() {
    const name = document.getElementById('up-name').value;
    const surname = document.getElementById('up-surname').value;
    const password = document.getElementById('up-pass').value;

    const updateData = { name, surname, email: currentCustomerEmail };
    if(password) updateData.password = password;

    try {
        // Controller-dəki PUT /api/customers/profile?email=... endpoint-inə uyğunlaşdırıldı
        const res = await fetch(`http://95.111.230.66:8080/api/customers/profile?email=${encodeURIComponent(currentCustomerEmail)}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updateData)
        });

        if(res.ok) {
            alert("Məlumatlarınız uğurla yeniləndi!");
            location.reload();
        } else {
            alert("Yenilənmə zamanı xəta: " + res.status);
        }
    } catch(err) { alert("Şəbəkə xətası."); }
};

// --- ALDIĞIM KOMPÜTERLƏR (GET /api/customers/v1) ---
window.getMyComputers = async function() {
    const container = document.getElementById("pc-list-content");
    hideAllSections();
    document.getElementById("my-computers-area").style.display = "block";
    container.innerHTML = "<p>Yüklənir...</p>";

    try {
        // Controller-dəki @GetMapping("/v1") çağrılır
        const res = await fetch('http://95.111.230.66:8080/api/customers/v1', { 
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (res.ok) {
            const boughtItems = await res.json();
            if (!boughtItems || boughtItems.length === 0) {
                container.innerHTML = "<p>Hələ ki heç bir kompüter almamısınız.</p>";
                return;
            }
            container.innerHTML = boughtItems.map(pc => `
                <div style="background:#161b22; padding:15px; margin-bottom:10px; border-radius:8px; border-left:4px solid #8957e5;">
                    <span style="color:white; font-weight:bold;">${pc.name}</span>
                    <b style="color:#49fb35;">${pc.price} AZN</b>
                </div>
            `).join('');
        }
    } catch (err) { container.innerHTML = "<p>Şəbəkə xətası.</p>"; }
};

// --- SATDIĞIM KOMPÜTERLƏR (GET /api/customers/selling) ---
window.getSellingComputers = function() {
    const container = document.getElementById("pc-list-content-selling");
    hideAllSections();
    document.getElementById("my-computers-area-selling").style.display = "block";
    container.innerHTML = "<p>Yüklənir...</p>";

    fetch('http://95.111.230.66:8080/api/customers/selling', {
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(ads => {
        container.innerHTML = ads.length === 0 ? "<p>Elanınız yoxdur.</p>" : ads.map(pc => `
            <div style="padding:15px; margin-bottom:10px; border-radius:10px; border:1px solid #30363d;">
                <h4>${pc.name}</h4>
                <p>${pc.price} AZN</p>
                <button onclick="deleteAd(${pc.id})" style="background:#da3633; color:white; border:none; padding:5px;">Sil</button>
            </div>
        `).join('');
    });
};

// --- HESABI SİL (DELETE /api/customers/delete) ---
window.deleteAccount = async function() {
    if(!confirm("Hesabınızı silmək istəyirsiniz?")) return;
    try {
        const res = await fetch('http://95.111.230.66:8080/api/customers/delete', {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if(res.ok) { window.logout(); }
    } catch(err) { alert("Xəta baş verdi."); }
};

function hideAllSections() {
    ['update-form', 'my-computers-area', 'my-computers-area-selling'].forEach(id => {
        const el = document.getElementById(id);
        if(el) el.style.display = 'none';
    });
}