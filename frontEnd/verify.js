async function verify() {
    const email = localStorage.getItem("pendingEmail");
    const result = document.getElementById("result");
    const codeInput = document.getElementById("otp-code");

    if (!codeInput) {
        console.error("otp-code inputu tapılmadı");
        return;
    }

    const code = codeInput.value.trim();

    if (!email) {
        alert("Email tapılmadı, yenidən qeydiyyatdan keçin.");
        window.location.href = "register.html";
        return;
    }

    if (!code || code.length !== 6) {
        result.style.color = "#f87171";
        result.innerText = "Zəhmət olmasa 6 rəqəmli kodu daxil edin.";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/users/verify", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, code: code })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.removeItem("pendingEmail");
            result.style.color = "green";
            result.innerText = "Email təsdiqləndi! Giriş səhifəsinə yönləndirilirsiniz...";
            setTimeout(() => { window.location.href = "login.html"; }, 1500);
        } else {
            result.style.color = "#f87171";
            result.innerText = data.message || "Kod yanlışdır";
        }
    } catch (err) {
        console.error("Verify xətası:", err);
        result.style.color = "#f87171";
        result.innerText = "Bağlantı xətası! Serveri yoxlayın.";
    }
}

async function resendOTP() {
    const email = localStorage.getItem("pendingEmail");
    const result = document.getElementById("result");

    if (!email) {
        alert("Sessiya bitib, yenidən qeydiyyatdan keçin.");
        window.location.href = "register.html";
        return;
    }

    try {
        const response = await fetch("http://localhost:8080/api/users/resendOTP", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email })
        });

        if (response.ok) {
            result.style.color = "green";
            result.innerText = "Yeni kod emailinizə göndərildi!";
        } else {
            const data = await response.json();
            result.style.color = "#f87171";
            result.innerText = data.message || "Kod göndərilə bilmədi!";
        }
    } catch (err) {
        console.error("Resend xətası:", err);
        result.style.color = "#f87171";
        result.innerText = "Bağlantı xətası! İnterneti yoxlayın.";
    }
}