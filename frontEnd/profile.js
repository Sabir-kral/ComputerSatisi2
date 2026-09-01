const API_BASE = "http://95.111.230.66:8080/api";

// Tokeni obyekt daxilindən təhlükəsiz və düzgün şəkildə dartırıq
let token = localStorage.getItem('accessToken');
if (!token) {
    const activeUserStr = localStorage.getItem('activeUser');
    if (activeUserStr) {
        try {
            const activeUser = JSON.parse(activeUserStr);
            token = activeUser.accessToken;
        } catch(e) {
            console.error("User token oxunarkən xəta:", e);
        }
    }
}

if (!token) window.location.href = 'login.html';

let currentCustomerEmail = "";
let currentEditingComputerId = null;

document.addEventListener('DOMContentLoaded', loadProfile);

// --- PROFİL MƏLUMATLARINI YÜKLƏ ---
async function loadProfile() {
    try {
        const response = await fetch(`${API_BASE}/customers/profile`, {
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
        } else {
            logout();
        }
    } catch (err) { 
        console.error("Profil yüklənərkən xəta baş verdi:", err); 
    }
}

// --- HTML-dəki "Məlumatları Yenilə" düyməsinin çağırdığı funksiya ---
window.showUpdate = function() {
    hideAllSections();
    const updateForm = document.getElementById("update-form");
    if(updateForm) updateForm.style.display = "block";
};

// --- REDAKTƏ EDİB YADDA SAXLA ---
window.processUpdate = async function() {
    const name = document.getElementById('up-name').value;
    const surname = document.getElementById('up-surname').value;
    const password = document.getElementById('up-pass').value;

    if(!name || !surname) {
        alert("Ad və Soyad boş qala bilməz!");
        return;
    }

    if(!currentCustomerEmail) {
        alert("İstifadəçi emaili tapılmadı, zəhmət olmasa səhifəni yeniləyin.");
        return;
    }

    const updateData = { 
        name: name, 
        surname: surname,
        email: currentCustomerEmail 
    };
    
    if(password) {
        updateData.password = password; 
    }

    try {
        const res = await fetch(`${API_BASE}/customers/profile?email=${encodeURIComponent(currentCustomerEmail)}`, {
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
            alert("Yenilənmə zamanı xəta baş verdi (Status: " + res.status + ")");
        }
    } catch(err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

// --- SATDIĞIM KOMPÜTERLƏRİ GƏTİR ---
window.getSellingComputers = function() {
    const container = document.getElementById("pc-list-content-selling");
    hideAllSections();
    
    const areaSelling = document.getElementById("my-computers-area-selling");
    if(areaSelling) areaSelling.style.display = "block";
    
    container.innerHTML = "<p>Yüklənir...</p>";

    fetch(`${API_BASE}/customers/selling`, {
        headers: { 'Authorization': `Bearer ${token}` }
    })
    .then(res => res.json())
    .then(ads => {
        if (!ads || ads.length === 0) {
            container.innerHTML = "<p>Heç bir elanınız yoxdur.</p>";
            return;
        }

        container.innerHTML = ads.map(pc => `
            <div style="background:#161b22; border:1px solid #30363d; padding:15px; margin-bottom:10px; border-radius:10px; display:flex; justify-content:space-between; align-items:center;">
                <div>
                    <h4 style="color:white; margin:0;">${pc.name}</h4>
                    <p style="color:#49fb35; margin:5px 0 0 0;">${pc.price} AZN</p>
                </div>
                <div style="display:flex; gap:8px;">
                    <button onclick='openEditModal(${pc.id}, ${JSON.stringify(pc.name)}, ${pc.price}, ${JSON.stringify(pc.description || "")})' style="background:#58a6ff; color:#000; border:none; padding:6px 12px; border-radius:5px; cursor:pointer; font-weight:bold;">Redaktə</button>
                    <button onclick="deleteAd(${pc.id})" style="background:#da3633; color:white; border:none; padding:6px 12px; border-radius:5px; cursor:pointer;">Sil</button>
                </div>
            </div>
        `).join('');
    })
    .catch(err => { 
        container.innerHTML = "<p>Məlumat gəlmədi (403 və ya Şəbəkə xətası).</p>"; 
    });
};

// --- KOMPÜTER REDAKTƏ MODALINI AÇ ---
window.openEditModal = function(id, name, price, description) {
    currentEditingComputerId = id;
    document.getElementById('update-pc-name').value = name;
    document.getElementById('update-pc-price').value = price;
    document.getElementById('update-pc-desc').value = description;
    document.getElementById('updateComputerModal').style.display = 'flex';
};

// --- MODALI BAĞLA ---
window.closeUpdateModal = function() {
    document.getElementById('updateComputerModal').style.display = 'none';
    currentEditingComputerId = null;
};

// --- KOMPÜTER REDAKTƏSİNİ YADDA SAXLA ---
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
            alert("Kompüter uğurla yeniləndi!");
            closeUpdateModal();
            getSellingComputers();
        } else {
            alert("Yenilənmə zamanı xəta baş verdi (Status: " + res.status + ")");
        }
    } catch (err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

// --- SƏBƏTİ GƏTİR (BACKEND-DƏN) ---
window.getMyCart = async function() {
    const container = document.getElementById("pc-list-content");
    const checkoutSummary = document.getElementById("checkout-summary");
    
    hideAllSections(); 
    const myCcArea = document.getElementById("my-computers-area");
    if(myCcArea) myCcArea.style.display = "block"; 
    
    container.innerHTML = "<p>Yüklənir...</p>";
    if(checkoutSummary) checkoutSummary.style.display = "none"; 

    try {
        const res = await fetch(`${API_BASE}/cart/my-cart`, { 
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (res.ok) {
            const cartItems = await res.json();
            
            if (!cartItems || cartItems.length === 0) {
                container.innerHTML = "<p>Səbətiniz boşdur.</p>";
                return;
            }
            
            container.innerHTML = cartItems.map(item => {
                const computer = item.computer || {};
                const lineTotal = (Number(computer.price) || 0) * (item.quantity || 1);
                return `
                <div style="background:#161b22; padding:15px; margin-bottom:10px; display:flex; justify-content:space-between; align-items:center; border-radius:8px; border-left: 4px solid #8957e5;">
                    <div>
                        <span style="color:white; font-weight:bold;">${computer.name || 'Kompüter'}</span>
                        <span style="color:#8b949e; margin-left:8px;">x${item.quantity || 1}</span>
                    </div>
                    <div style="display:flex; align-items:center; gap:12px;">
                        <b style="color:#49fb35;">${lineTotal} AZN</b>
                        <button onclick="removeCartItem(${computer.id})" style="background:#da3633; color:white; border:none; padding:6px 12px; border-radius:5px; cursor:pointer;">Sil</button>
                    </div>
                </div>`;
            }).join('');

            let totalPrice = 0;
            cartItems.forEach(item => {
                const computer = item.computer || {};
                totalPrice += (Number(computer.price) || 0) * (item.quantity || 1);
            });

            const totalPriceDisplay = document.getElementById('total-price-display');
            if (totalPriceDisplay && totalPrice > 0) {
                totalPriceDisplay.innerText = totalPrice;
                if(checkoutSummary) checkoutSummary.style.display = "block"; 
            }
        } else {
            container.innerHTML = "<p>Məlumat gəlmədi (Status: " + res.status + ")</p>";
        }
    } catch (err) {
        console.error("Xəta:", err);
        container.innerHTML = "<p>Şəbəkə xətası baş verdi.</p>";
    }
};

// --- SƏBƏTDƏN MƏHSUL SİL ---
window.removeCartItem = async function(computerId) {
    if (!confirm("Bu məhsulu səbətdən silmək istədiyinizdən əminsiniz?")) return;
    try {
        const res = await fetch(`${API_BASE}/cart/remove?productId=${computerId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            getMyCart();
        } else {
            alert("Silinmə zamanı xəta oldu.");
        }
    } catch (err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

// --- ELAN SİLMƏK ---
window.deleteAd = async function(id) {
    if (!confirm("Bu elanı silmək istədiyinizdən əminsiniz?")) return;
    try {
        const res = await fetch(`${API_BASE}/computers/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            alert("Elan uğurla silindi.");
            window.getSellingComputers(); 
        } else {
            alert("Silinmə zamanı xəta oldu.");
        }
    } catch (err) { alert("Şəbəkə xətası baş verdi."); }
};

// --- HESABI SİLMƏK ---
window.deleteAccount = async function() {
    if(!confirm("Hesabınızı silmək istədiyinizdən əminsiniz?")) return;
    try {
        const res = await fetch(`${API_BASE}/customers/delete`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if(res.ok) {
            alert("Hesabınız silindi.");
            window.logout();
        } else {
            alert("Silinmə zamanı xəta oldu.");
        }
    } catch(err) { alert("Xəta baş verdi."); }
};

// --- NAVİQASİYA VƏ ÇIXIŞ ---
window.addComputer = () => window.location.href = 'add-computer.html';
window.goToCheckout = () => window.location.href = 'checkout.html';
window.logout = () => { 
    localStorage.removeItem('accessToken'); 
    localStorage.removeItem('selectedPc'); 
    window.location.href = 'index.html'; 
};

window.goBack = function() {
    window.location.href = 'index.html';
};

function hideAllSections() {
    ['update-form', 'my-computers-area', 'my-computers-area-selling', 'selling-area'].forEach(id => {
        const el = document.getElementById(id);
        if(el) el.style.display = 'none';
    });
}

// --- ADMIN OLUB-OLMADIĞINI YOXLA ---
(async function checkAdminAccess() {
    
    try {
        const token = localStorage.getItem("accessToken")
        const res = await fetch(`${API_BASE}/admin/check`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            const btn = document.getElementById('admin-panel-btn');
            if (btn) btn.style.display = 'inline-flex';
        }
    } catch (err) {
                    const btn = document.getElementById('admin-panel-btn');
            if (btn) btn.style.display = 'inline-flex';
    }
})();
function admin(){
    window.location.href="admin.html"
}