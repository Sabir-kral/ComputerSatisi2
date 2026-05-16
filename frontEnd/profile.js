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

// Əgər hələ də token yoxdursa, login səhifəsinə atır
if (!token) window.location.href = 'login.html';

// Yeniləmə zamanı backend-ə ötürmək üçün istifadəçinin emailini burada saxlayacağıq
let currentCustomerEmail = "";

document.addEventListener('DOMContentLoaded', loadProfile);

// --- PROFİL MƏLUMATLARINI YÜKLƏ ---
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
            
            // Email-i qlobal dəyişənə mənimsədirik ki, update edəndə istifadə edə bilək
            currentCustomerEmail = user.email;

            // Update inputlarını doldururuq
            if(document.getElementById('up-name')) document.getElementById('up-name').value = user.name;
            if(document.getElementById('up-surname')) document.getElementById('up-surname').value = user.surname;
        } else {
            logout();
        }
    } catch (err) { console.error("Profil xətası."); }
}

// --- HTML-dəki "Məlumatları Yenilə" düyməsinin çağırdığı funksiya ---
window.showUpdate = function() {
    hideAllSections();
    const updateForm = document.getElementById("update-form");
    if(updateForm) updateForm.style.display = "block";
};

// --- REDAKTƏ EDİB YADDA SAXLA (Java-ya Tam Uyğunlaşdırılmış) ---
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

    // Java-dakı CustomerRequest obyektinə uyğun bədən (body)
    // Qeyd: Əgər backend CustomerRequest-də şifrə sahəsini fərqli adlandırıbsa (məs: password), bura yazılmalıdır.
    const updateData = { 
        name: name, 
        surname: surname,
        email: currentCustomerEmail // Request obyektinin boş qalmaması üçün email-i də daxil edirik
    };
    
    if(password) {
        updateData.password = password; 
    }

    try {
        // Backend @RequestParam String email gözlədiyi üçün URL-ə ?email= dəyərini əlavə edirik
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
            alert("Yenilənmə zamanı xəta baş verdi (Status: " + res.status + ")");
        }
    } catch(err) {
        alert("Şəbəkə xətası baş verdi.");
    }
};

// --- SATDIĞIM KOMPÜTERLƏRİ GƏTİR ---
window.getSellingComputers = async function() {
    const container = document.getElementById("pc-list-content-selling");
    hideAllSections();
    document.getElementById("my-computers-area-selling").style.display = "block";
    container.innerHTML = "<p>Yüklənir...</p>";

    try {
        const res = await fetch('http://95.111.230.66:8080/api/customers/selling', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const ads = await res.json();
        
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
                <button onclick="deleteAd(${pc.id})" style="background:#da3633; color:white; border:none; padding:6px 12px; border-radius:5px; cursor:pointer;">Sil</button>
            </div>
        `).join('');
    } catch (err) { container.innerHTML = "<p>Məlumat gəlmədi.</p>"; }
};

// --- ALDIĞIM KOMPÜTERLƏR ---
// --- ALDIĞIM KOMPÜTERLƏR (Java ilə Tam Sinxronlaşdırılmış) ---
window.getMyComputers = async function() {
    const container = document.getElementById("pc-list-content");
    const checkoutSummary = document.getElementById("checkout-summary");
    
    hideAllSections(); 
    document.getElementById("my-computers-area").style.display = "block"; 
    container.innerHTML = "<p>Yüklənir...</p>";
    if(checkoutSummary) checkoutSummary.style.display = "none"; // İlkin olaraq gizlədək

    try {
        // Java Controller-dəki @GetMapping("/v1") endpointinə sorğu atırıq (getAllBought)
        const res = await fetch('http://95.111.230.66:8080/api/customers/v1', { 
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (res.ok) {
            const boughtItems = await res.json();
            
            if (!boughtItems || boughtItems.length === 0) {
                container.innerHTML = "<p>Hələ ki real olaraq heç bir kompüter almamısınız.</p>";
                return;
            }
            
            // Alınan kompüterləri ekranda göstəririk
            container.innerHTML = boughtItems.map(pc => `
                <div style="background:#161b22; padding:15px; margin-bottom:10px; display:flex; justify-content:space-between; border-radius:8px; border-left: 4px solid #8957e5; align-items:center;">
                    <span style="color:white; font-weight:bold;">${pc.name}</span>
                    <b style="color:#49fb35;">${pc.price} AZN (Alındı)</b>
                </div>
            `).join('');

            // --- SƏBƏT CƏMİ MƏNTİQİ ---
            // Əgər bu kompüterlər hələ "Səbət" statusundadırsa və ümumi qiymət göstərmək lazımdırsa:
            let totalPrice = 0;
            boughtItems.forEach(item => {
                totalPrice += Number(item.price) || 0;
            });

            const totalPriceDisplay = document.getElementById('total-price-display');
            if (totalPriceDisplay && totalPrice > 0) {
                totalPriceDisplay.innerText = totalPrice;
                if(checkoutSummary) checkoutSummary.style.display = "block"; // Sifariş formunu açırıq
            }

        } else {
            container.innerHTML = "<p>Məlumat gəlmədi (Status: " + res.status + ")</p>";
        }
    } catch (err) {
        console.error("Kompüterləri gətirərkən xəta:", err);
        container.innerHTML = "<p>Şəbəkə xətası səbəbindən məlumat gəlmədi.</p>";
    }
};

// --- ELAN SİLMƏK ---
window.deleteAd = async function(id) {
    if (!confirm("Bu elanı silmək istədiyinizdən əminsiniz?")) return;
    try {
        const res = await fetch(`http://95.111.230.66:8080/api/computers/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            alert("Elan uğurla silindi.");
            getSellingComputers(); 
        } else {
            alert("Silinmə zamanı xəta oldu.");
        }
    } catch (err) { alert("Şəbəkə xətası baş verdi."); }
};

// --- HESABI SİLMƏK (Java-dakı /delete endpointinə uyğunlaşdırıldı) ---
window.deleteAccount = async function() {
    if(!confirm("Hesabınızı silmək istədiyinizdən əminsiniz?")) return;
    try {
        const res = await fetch('http://95.111.230.66:8080/api/customers/delete', {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if(res.ok) {
            alert("Hesabınız silindi.");
            logout();
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

// Geri düyməsinin funksiyası
window.goBack = function() {
    window.location.href = 'index.html';
};

function hideAllSections() {
    ['update-form', 'my-computers-area', 'my-computers-area-selling', 'selling-area'].forEach(id => {
        const el = document.getElementById(id);
        if(el) el.style.display = 'none';
    });
}