const API_BASE = "http://localhost:8080/api";
let currentIdx = 0;
let productImages = [];

document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    fetchComputers();
});

function checkAuth() {
    const token = localStorage.getItem("token");
    const email = localStorage.getItem("email");
    const loginBtn = document.getElementById("loginBtn");
    const userProfile = document.getElementById("userProfile");
    const userInitial = document.getElementById("userInitial");

    if (token && email) {
        if(loginBtn) loginBtn.classList.add("hidden");
        userProfile.classList.remove("hidden");
        userInitial.innerText = email.charAt(0).toUpperCase();
    } else {
        if(loginBtn) loginBtn.classList.remove("hidden");
        userProfile.classList.add("hidden");
    }
}

async function fetchComputers() {
    try {
        const res = await fetch(`${API_BASE}/customers/v2`);
        const data = await res.json();
        const container = document.getElementById("computer-container");
        
        container.innerHTML = data.map(pc => `
            <div class="pc-card" onclick="openDetails(${pc.id})">
                <h2 style="color:white; margin:0">${pc.name}</h2>
                <div style="height:2px; width:40px; background:var(--primary-blue); margin:10px 0"></div>
                <p style="color:var(--primary-blue); font-size:1.4rem; font-weight:bold">${pc.price} AZN</p>
            </div>
        `).join('');
    } catch (err) { console.error("Xəta:", err); }
}
async function openDetails(id) {
    const overlay = document.getElementById("detailOverlay");
    const content = document.getElementById("detailContent");
    const token = localStorage.getItem("token") || localStorage.getItem("accessToken");

    overlay.style.display = "flex";
    overlay.style.opacity = "0";
    content.innerHTML = "<h1 style='color:white'>Yüklənir...</h1>";

    // Fade in overlay
    setTimeout(() => { overlay.style.opacity = "1"; overlay.style.transition = "opacity 0.3s"; }, 10);

    try {
        const res = await fetch(`${API_BASE}/computers/${id}`, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });
        const pc = await res.json();
        const img = pc.imageLinks?.[0] || 'https://via.placeholder.com/450x300?text=No+Image';

        content.innerHTML = `
            <div class="left-side">
                <img src="${img}" class="detail-img">
                <h1 class="detail-name">${pc.name}</h1>
            </div>
            <div class="right-side">
                <div class="detail-desc">${pc.description}</div>
                <div class="detail-price">${pc.price} AZN</div>
                <div style="display:flex; gap:15px; margin-top:25px;">
                    <button class="back-btn" onclick="closeDetails()">← GERİ</button>
                    <button class="back-btn" style="background:#49fb35; color:#000;" onclick="buyComputer(${pc.id})">İNDİ AL</button>
                </div>
            </div>
        `;

        // Wobble when hitting edges
        content.classList.remove("wobble");
        void content.offsetWidth; // reflow
        content.classList.add("wobble");
        createBalls();

    } catch (e) {
        closeDetails();
    }
}

function closeDetails() {
    const overlay = document.getElementById("detailOverlay");
    overlay.style.opacity = "0";
    setTimeout(() => {
        overlay.style.display = "none";
        overlay.style.opacity = "1";
        document.querySelectorAll(".jelly-ball").forEach(b => b.remove());
    }, 300);
}

// Wobble on drag to edges
document.addEventListener("DOMContentLoaded", () => {
    const overlay = document.getElementById("detailOverlay");
    const content = document.getElementById("detailContent");
    let isDragging = false;
    let startX, startY, currentX = 0, currentY = 0;

    content.addEventListener("mousedown", (e) => {
        isDragging = true;
        startX = e.clientX - currentX;
        startY = e.clientY - currentY;
        content.style.cursor = "grabbing";
    });

    document.addEventListener("mousemove", (e) => {
        if (!isDragging) return;
        currentX = e.clientX - startX;
        currentY = e.clientY - startY;

        const maxX = (window.innerWidth - content.offsetWidth) / 2;
        const maxY = (window.innerHeight - content.offsetHeight) / 2;

        // Wobble when hitting edges
        if (Math.abs(currentX) > maxX * 0.8 || Math.abs(currentY) > maxY * 0.8) {
            content.classList.remove("wobble");
            void content.offsetWidth;
            content.classList.add("wobble");
            currentX = Math.sign(currentX) * maxX * 0.8;
            currentY = Math.sign(currentY) * maxY * 0.8;
        }

        content.style.transform = `translate(${currentX}px, ${currentY}px)`;
    });

    document.addEventListener("mouseup", () => {
        if (!isDragging) return;
        isDragging = false;
        content.style.cursor = "grab";
        // Snap back to center
        content.style.transition = "transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1)";
        content.style.transform = "translate(0, 0)";
        currentX = 0; currentY = 0;
        setTimeout(() => { content.style.transition = ""; }, 400);
    });
});