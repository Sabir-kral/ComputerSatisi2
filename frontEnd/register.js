document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('name').value;
    const surname = document.getElementById('surname').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const response = await fetch('https://denatured-depress-munchkin.ngrok-free.dev/api/customers', { 
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'ngrok-skip-browser-warning': 'true'
            },
            body: JSON.stringify({ name, surname, email, password })
        });

        if (response.ok) {
            alert("Qeydiyyat uğurludur! Kod daxil edin.");
            localStorage.setItem("pendingEmail", email);
            window.location.href = "verify.html";
        } else {
            const data = await response.json();
            alert("Xəta: " + (data.message || "Məlumatlar yanlışdır!"));
        }
    } catch (err) {
        alert("Xəta baş verdi: " + err.message);
    }
});