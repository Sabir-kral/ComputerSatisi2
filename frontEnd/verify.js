// Əgər formun içindədirsə, 'e' parametrini ötür:
async function verify(e) {
    if (e) e.preventDefault(); // Səhifənin refresh olmasını dayandırır

    // Register-də 'pendingEmail' olaraq yadda saxlamışdıq, bura düzəliş etdik:
    const email = window.localStorage.getItem("pendingEmail");
    const code = document.getElementById("code").value;

    if (!email) {
        alert("Email tapılmadı, yenidən qeydiyyatdan keçin.");
        window.location.href = "register.html";
        return;
    }

    const request = {
        "email": email,
        "code": code
    };

    try {
        // PORTU 8080 ETDİK (Backend-ə müraciət üçün)
        const response = await fetch("http://localhost:8080/api/users/verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(request)
        });

        const data = await response.json();

        if (response.ok) {
            window.localStorage.removeItem("pendingEmail");
            window.localStorage.setItem("accessToken", data.accessToken);
            document.getElementById("result").innerText = "Xoş gəldiniz";
            document.getElementById("result").style.color = "green";
            // Bir az gözləyib sonra yönləndirmək daha yaxşıdır
            setTimeout(() => { window.location.href = "index.html"; }, 1000);
        } else {
            document.getElementById("result").style.color = "#f87171";
            document.getElementById("result").innerText = data.message || "Kod yanlışdır";
        }
    } catch (err) {
        console.error("Verify xətası:", err);
        alert("Bağlantı xətası!");
    }
}
async function resendOTP() {
    const email = localStorage.getItem('pendingEmail');
    
    if (!email) {
        alert("Sessiya bitib, yenidən qeydiyyatdan keçin.");
        window.location.href = "register.html";
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/users/resendOtp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            // Backend ResendOtpRequest gözləyir: { "email": "..." }
            body: JSON.stringify({ email: email }) 
        });

        if (response.ok) {
            alert("Yeni 6 rəqəmli təsdiq kodu emailinizə göndərildi!");
        } else {
            const data = await response.json();
            alert("Xəta: " + (data.message || "Kod göndərilə bilmədi!"));
        }
    } catch (err) {
        console.error("Resend xətası:", err);
        alert("Bağlantı xətası! İnterneti yoxlayın.");
    }
}