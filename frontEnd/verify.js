async function verify(e) {
    if (e) e.preventDefault();

    const email = localStorage.getItem("pendingEmail");
    const code = document.getElementById("code").value;

    if (!email) {
        alert("Email tapılmadı, yenidən qeydiyyatdan keçin.");
        window.location.href = "register.html";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/users/verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, code })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.removeItem("pendingEmail");
            document.getElementById("result").innerText = "Email təsdiqləndi! Giriş səhifəsinə yönləndirilirsiniz...";
            document.getElementById("result").style.color = "green";
            // Verify returns MessageResponse, NOT a token — redirect to login
            setTimeout(() => { window.location.href = "login.html"; }, 1500);
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
        const response = await fetch('http://localhost:8080/api/users/resendOTP', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
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