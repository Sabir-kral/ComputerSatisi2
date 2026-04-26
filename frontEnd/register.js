document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const surname = document.getElementById('surname').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    if (!email || !password) {
        alert("Email və şifrə mütləqdir!");
        return;
    }

    try {
        // QEYD: Backend-də endpoint '/api/customers' və ya '/api/auth/register' ola bilər.
        // Sənin koduna əsasən '/api/customers' saxladım.
        const response = await fetch('https://denatured-depress-munchkin.ngrok-free.dev/api/customers', { 
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'ngrok-skip-browser-warning': 'true' // NGROK XƏTASINI DÜZƏLDƏN SƏTİR
            },
            body: JSON.stringify({ name, surname, email, password })
        });

        // Cavab JSON deyilsə tutmaq üçün (Unexpected token '<' xətasının qarşısını alır)
        const contentType = response.headers.get("content-type");
        if (!contentType || !contentType.includes("application/json")) {
            const text = await response.text();
            console.error("Server JSON qaytarmadı! Gələn cavab:", text);
            throw new Error("Server xətası: Gözlənilməz cavab formatı.");
        }

        const data = await response.json();

        if (response.ok) {
            alert("Qeydiyyat uğurludur! Gmailinizə gələn kodu daxil edin.");
            localStorage.setItem("pendingEmail", email);
            window.location.href = "verify.html";
        } else {
            alert("Xəta: " + (data.message || "Məlumatlar yanlışdır!"));
        }

    } catch (err) {
        console.error("Şəbəkə xətası:", err);
        alert("Xəta baş verdi: " + err.message);
    }
});