document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("register-form");

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const userData = {
                name: document.getElementById('name').value,
                surname: document.getElementById('surname').value,
                email: document.getElementById('reg-email').value,
                password: document.getElementById('reg-password').value
            };

            console.log("Qeydiyyat üçün göndərilir:", userData);

            try {
                const response = await fetch("http://95.111.230.66:8080/api/customers", {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(userData)
                });

                // Cavabı yalnız BİR DƏFƏ oxuyuruq
                if (response.ok) {
                    // Əgər backend JSON qaytarırsa
                    const data = await response.json(); 
                    console.log("Backend-dən gələn data:", data);
                    
                    alert("Təbriklər! Qeydiyyat uğurla tamamlandı.");
                    
                    // Email-i yadda saxla (data.email və ya userData.email)
                    localStorage.setItem("pendingEmail", userData.email);
                    window.location.href = "verify.html";
                } else {
                    // Xəta halında mesajı mətni kimi oxuyuruq
                    const errorMsg = await response.text();
                    console.error("Backend xətası:", response.status, errorMsg);
                    alert("Xəta baş verdi: " + errorMsg);
                }
            } catch (err) {
                // Əgər bura düşürsənsə, deməli ya kodda sintaksis səhvi var, ya da internet kəsilib
                console.error("Detallı xəta:", err);
                alert("Sistem xətası! Konsola (F12) baxın.");
            }
        });
    }
});