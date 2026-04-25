document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const surname = document.getElementById('surname').value; // add surname field to your HTML if missing
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const response = await fetch('http://localhost:8080/api/customers', { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, surname, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            alert("Qeydiyyat uğurludur! Gmailinizə gələn kodu daxil edin.");
            localStorage.setItem("pendingEmail", email);
            window.location.href = "verify.html";
        } else {
            alert("Xəta: " + (data.message || "Bu məlumatlarla qeydiyyat mümkün olmadı!"));
        }

    } catch (err) {
        console.error("Şəbəkə xətası:", err);
        alert("Serverə qoşulmaq mümkün olmadı!");
    }
});