document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const surname = document.getElementById('surname').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    // ADD THIS - check values before sending
    console.log("name:", name);
    console.log("surname:", surname);
    console.log("email:", email);
    console.log("password:", password);

    if (!email) {
        alert("Email boşdur!");
        return;
    }

    try {
        const response = await fetch('https://denatured-depress-munchkin.ngrok-free.dev/api/customers', { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' ,
                        'ngrok-skip-browser-warning': 'true'
            },
            body: JSON.stringify({ name, surname, email, password })
            });

        const data = await response.json();

        if (response.ok) {
            alert("Qeydiyyat uğurludur! Gmailinizə gələn kodu daxil edin.");
            localStorage.setItem("pendingEmail", email);
            console.log("Saved email to localStorage:", email);
            window.location.href = "verify.html";
        } else {
            alert("Xəta: " + (data.message || "Bu məlumatlarla qeydiyyat mümkün olmadı!"));
        }

    } catch (err) {
        console.error("Şəbəkə xətası:", err);
        alert("Serverə qoşulmaq mümkün olmadı!");
    }
});