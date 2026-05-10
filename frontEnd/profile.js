const token = localStorage.getItem('accessToken');
if (!token) location.href = 'login.html';

// Səhifə açılan kimi profili yüklə
loadProfile();

async function loadProfile() {
    try {
        const response = await fetch('http://95.111.230.66:8080/api/customers/profile', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) logout();
            return;
        }
        const user = await response.json();
        document.getElementById('view-name').innerText = user.name;
        document.getElementById('view-surname').innerText = user.surname;
        document.getElementById('view-email').innerText = user.email;
    } catch (err) {
        console.error("Profil məlumatları gəlmədi.");
    }
}

// --- HESAB MƏLUMATLARINI YENİLƏ ---
function showUpdate() {
    hideAllSections();
    document.getElementById('profile-view').style.display = 'none';
    document.getElementById('update-form').style.display = 'block';
}

async function processUpdate() {
    const currentEmail = document.getElementById('view-email').innerText;
    const data = {
        name: document.getElementById('up-name').value,
        surname: document.getElementById('up-surname').value,
        email: currentEmail,
        password: document.getElementById('up-pass').value
    };

    try {
        const response = await fetch(`http://95.111.230.66:8080/api/customers/profile?email=${encodeURIComponent(currentEmail)}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert("Profil yeniləndi!");
            location.reload();
        } else {
            alert("Yeniləmə zamanı xəta!");
        }
    } catch (err) { alert("Bağlantı xətası!"); }
}

// --- HESABI TAMAMİLƏ SİLMƏ ---
async function deleteAccount() {
    if (confirm("Hesabınızı və bütün elanlarınızı silmək istədiyinizə əminsiniz?")) {
        try {
            const res = await fetch('http://95.111.230.66:8080/api/customers/delete', {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                alert("Hesabınız silindi.");
                // Hesab silinəndə hər şeyi təmizləmək olar
                localStorage.clear(); 
                location.href = 'index.html';
            }
        } catch (err) { alert("Silmə zamanı xəta!"); }
    }
}

// --- SƏBƏT (ALDIĞIM KOMPÜTERLƏR) İDARƏSİ ---
function getMyComputers() {
    const area = document.getElementById("my-computers-area");
    const container = document.getElementById("pc-list-content");
    const summaryArea = document.getElementById("checkout-summary");

    hideAllSections();
    area.style.display = "block";

    // Səbəti lokal yaddaşdan oxuyuruq
    const cartItems = JSON.parse(localStorage.getItem("userCart")) || [];

    if (cartItems.length === 0) {
        container.innerHTML = "<p style='color:#8b949e; text-align:center;'>Səbətiniz hazırda boşdur.</p>";
        if(summaryArea) summaryArea.style.display = "none";
        return;
    }

    if(summaryArea) summaryArea.style.display = "block";
    let totalPrice = 0;

    container.innerHTML = cartItems.map((pc, index) => {
        totalPrice += parseFloat(pc.price);
        return `
        <div style="background:#161b22; border:1px solid #30363d; border-radius:10px; padding:15px; margin-bottom:10px; display:flex; align-items:center; gap:15px;">
            <img src="${pc.img || 'placeholder.png'}" style="width:60px; height:60px; object-fit:cover; border-radius:5px;">
            <div style="flex:1;">
                <h4 style="margin:0; color:white;">${pc.name}</h4>
                <p style="margin:0; color:#58a6ff; font-weight:bold;">${pc.price} AZN</p>
            </div>
            <button onclick="removeFromCart(${index})" style="background:#da3633; color:white; border:none; padding:5px 12px; border-radius:5px; cursor:pointer; font-size:12px;">Səbətdən Sil</button>
        </div>`;
    }).join('');

    document.getElementById('total-price-display').innerText = totalPrice;
}

function removeFromCart(index) {
    let cartItems = JSON.parse(localStorage.getItem("userCart")) || [];
    cartItems.splice(index, 1); // Seçilən məhsulu massivdən çıxar
    localStorage.setItem("userCart", JSON.stringify(cartItems)); // Yenilənmiş səbəti yadda saxla
    getMyComputers(); // Siyahını yenilə
}

function goToCheckout() {
    const cartItems = JSON.parse(localStorage.getItem("userCart")) || [];
    if (cartItems.length === 0) {
        alert("Səbətiniz boşdur!");
        return;
    }
    location.href = 'checkout.html';
}

// --- KÖMƏKÇİ FUNKSİYALAR ---
function hideAllSections() {
    document.getElementById('update-form').style.display = 'none';
    document.getElementById('my-computers-area').style.display = 'none';
    document.getElementById('profile-view').style.display = 'block';
}

function logout() {
    // ƏSAS DÜZƏLİŞ: localStorage.clear() etmirik!
    // Səbətin (userCart) silinməməsi üçün yalnız tokeni silirik.
    localStorage.removeItem('accessToken'); 
    location.href = 'index.html';
}