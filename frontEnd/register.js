document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const response = await fetch('http://localhost:8080/api/customers', { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            // YALNIZ bura düşəndə uğurlu sayılır
            alert("Qeydiyyat uğurludur! Gmailinizə gələn kodu daxil edin.");
            localStorage.setItem("pendingEmail", email);
            window.location.href = "verify.html";
        } else {
            // Backend-dən gələn real xəta mesajını göstəririk
            alert("Xəta: " + (data.message || "Bu email artıq istifadə olunub!"));
        }
    } catch (err) {
        console.error("Qeydiyyat xətası:", err);
        alert("Serverə qoşulmaq mümkün olmadı!");
    }
});