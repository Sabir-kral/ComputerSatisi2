document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("register-form");

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            // Sənin HTML ID-lərinə uyğun məlumatları götürürük
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
                    headers: { 
                        'Content-Type': 'application/json' 
                    },
                    body: JSON.stringify(userData)
                });
                const data = response.json();

                if (response.ok) {
                    const text = await response.text();
                    // Əgər backend JSON qaytarsa onu parse edirik

                    alert("Təbriklər! Qeydiyyat uğurla tamamlandı.");
                    localStorage.setItem("pendingEmail",data.email)
                    window.location.href = "verify.html";
                } else {
                    const errorMsg = await response.text();
                    console.error("Backend xətası:", response.status, errorMsg);
                    alert("Xəta baş verdi: " + response.status + " (Spring Security-ni yoxlayın)");
                }
            } catch (err) {
                console.error("Şəbəkə xətası:", err);
                alert("Serverlə bağlantı kəsildi. HTTPS icazəsi verdiyinizdən əmin olun!");
            }
        });
    }
});