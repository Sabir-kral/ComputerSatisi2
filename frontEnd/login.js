document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('userEmail', data.email);
            alert("Giriş uğurludur!");
            window.location.href = "index.html"; // Ana səhifəyə yönləndir
        } else {
            alert("Xəta: " + (data.message || "Email və ya şifrə yanlışdır!"));
        }
    } catch (err) {
        alert("Serverə bağlanmaq mümkün olmadı!");
    }
});