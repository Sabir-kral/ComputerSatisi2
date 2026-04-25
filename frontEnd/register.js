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

        // 1. Əvvəlcə cavabı JSON-a çeviririk
        const data = await response.json();

        // 2. STATUS CHECK: Yalnız 200-299 arası uğurlu sayılır
        if (response.ok) {
            alert("Qeydiyyat uğurludur! Gmailinizə gələn kodu daxil edin.");
            localStorage.setItem("pendingEmail", email);
            window.location.href = "verify.html";
        } 
        // 3. Əgər 400 və ya digər xətalar gəlibsə
        else {
            // Backend-dən gələn mesajı (məs: "Email already exists") göstər
            alert("Xəta: " + (data.message || "Bu məlumatlarla qeydiyyat mümkün olmadı!"));
            // Burada kodun icrasını dayandırırıq, yönləndirmə baş vermir
        }

    } catch (err) {
        // Bu hissə ancaq internet kəsilsə və ya server sönülü olsa işləyir
        console.error("Şəbəkə xətası:", err);
        alert("Serverə qoşulmaq mümkün olmadı!");
    }
});