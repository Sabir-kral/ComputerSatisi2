const token = localStorage.getItem('accessToken');
if(!token) location.href = 'login.html';

async function loadProfile() {
    const response = await fetch('http://localhost:8080/api/customers/profile', {
        headers: {'Authorization': `Bearer ${token}`}
    });
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
    const data = {
        name: document.getElementById('up-name').value,
        surname: document.getElementById('up-surname').value,
        password: document.getElementById('up-pass').value // Əgər backend parol tələb edirsə
    };

    try {
        const response = await fetch('http://localhost:8080/api/customers/update', {
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
            alert("Yeniləmə uğursuz oldu: Giriş icazəniz yoxdur (403).");
        } else {
            alert("Xəta baş verdi.");
        }
    } catch (err) {
        console.error("Update xətası:", err);
    }
}
async function deleteAccount() {
    if(confirm("Hesabınız həmişəlik silinsin?")) {
        await fetch('http://localhost:8080/api/customers/delete', {
            method: 'DELETE',
            headers: {'Authorization': `Bearer ${token}`}
        });
        logout();
    }
}

function logout() {
    localStorage.clear();
    location.href = 'index.html';
}
async function getMyComputers() {
    const token = localStorage.getItem("token") || localStorage.getItem("accessToken");
    const area = document.getElementById("my-computers-area");
    const container = document.getElementById("pc-list-content");

    // Əvvəlcə yeri göstər
    area.style.display = "block";
    container.innerHTML = "<p style='color:white'>Yüklənir...</p>";

    try {
        const res = await fetch('http://localhost:8080/api/customers/v1', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (res.ok) {
            const computers = await res.json();
            
            if (computers.length === 0) {
                container.innerHTML = "<p>Sizin hələ ki, heç bir kompüteriniz yoxdur.</p>";
                return;
            }

            // Məlumatları HTML-ə doldur
            container.innerHTML = computers.map(pc => `
                <div class="pc-list-item">
                    <h4>${pc.name}</h4>
                    <p>${pc.description}</p>
                    <p class="pc-price">${pc.price} AZN</p>
                </div>
            `).join('');
            
            // Səhifəni aşağıya doğru sürüşdür ki, siyahı görünsün
            area.scrollIntoView({ behavior: 'smooth' });

        } else {
            container.innerHTML = "<p style='color:red'>Məlumatlar gətirilərkən xəta baş verdi.</p>";
        }
    } catch (err) {
        container.innerHTML = "<p style='color:red'>Serverlə bağlantı kəsildi.</p>";
    }
}


loadProfile();