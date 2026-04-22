  async function verify() {
            const email = window.localStorage.getItem("email");
            const code = document.getElementById("code").value;

            const request = {
                "email":email,
                "code":code
            };

            const response = await fetch("http://localhost:3000/api/users/verify", {
                method:"POST",
                headers:{"Content-Type":"application/json"},
                body: JSON.stringify(request)
            });

            const data = await response.json();

            if(response.ok) {
                window.localStorage.removeItem("email");
                window.localStorage.setItem("accessToken", data.accessToken);
                document.getElementById("result").innerText="Xoş gəldiniz";
                document.getElementById("result").style.color="green";
                window.location.href="index.html";
            }
            else {
                document.getElementById("result").style.color = "#f87171";
                document.getElementById("result").innerText = data.message;
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