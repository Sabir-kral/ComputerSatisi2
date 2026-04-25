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
    
    overlay.classList.add("active");
    content.innerHTML = "<h1 style='color:white'>Yüklənir...</h1>";

    const res = await fetch(`http://localhost:8080/api/computers/${id}`);
    const pc = await res.json();
    const mainImg = pc.imageLinks && pc.imageLinks.length > 0 ? pc.imageLinks[0] : 'https://via.placeholder.com/400';

    content.innerHTML = `
        <div class="left-side">
            <img src="${mainImg}" class="detail-img">
            <h1 class="detail-name">${pc.name}</h1>
        </div>
        <div class="right-side">
            <div class="detail-desc">
                <p>${pc.description}</p>
            </div>
            <h2 style="color:#58a6ff; margin-top:20px; font-size:2rem">${pc.price} AZN</h2>
            <button onclick="closeDetails()" style="margin-top:20px; background:#58a6ff; border:none; color:white; padding:10px 25px; border-radius:5px; cursor:pointer; font-weight:bold;">GERİ</button>
        </div>
    `;
    
    createJellyBalls();
}

function createJellyBalls() {
    const overlay = document.getElementById("detailOverlay");
    document.querySelectorAll(".jelly-ball").forEach(b => b.remove());

    for (let i = 0; i < 5; i++) {
        const ball = document.createElement("div");
        ball.className = "jelly-ball";
        const size = Math.random() * 120 + 100 + "px";
        ball.style.width = size;
        ball.style.height = size;
        ball.style.animationDuration = (Math.random() * 5 + 10) + "s";
        ball.style.animationDelay = (Math.random() * 5) + "s";
        overlay.appendChild(ball);
    }
}