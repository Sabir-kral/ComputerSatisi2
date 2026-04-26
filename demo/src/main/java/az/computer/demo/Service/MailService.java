package az.computer.demo.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void verifyEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlContent =
                "<div style='font-family: Arial, sans-serif; text-align:center; padding:20px;'>"
                        + "<img src='https://newapi.otogo.az/uploads/developia.jpg' width='60'/><br/><br/>"
                        + "<h2>Verify Email</h2>"
                        + "<p>Use the verification code below to verify your email:</p>"
                        + "<h1 style='letter-spacing:4px;'>" + token + "</h1>"
                        + "<p>This code will expire in 2 minutes.</p>"
                        + "<p style='font-size:12px; color:gray;'>If you didn't request this, ignore this email.</p>"
                        + "</div>";

        helper.setTo(email);
        helper.setSubject("Verify Email");
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }

    // Mövcud verifyEmail metodunun altına əlavə et:

    public void sendOrderNotifications(String buyerEmail, String sellerEmail, String computerName, double price,String buyerPhone) throws MessagingException {

        // 1. ALICIYA GÖNDƏRİLƏN MAİL
        MimeMessage buyerMsg = mailSender.createMimeMessage();
        MimeMessageHelper buyerHelper = new MimeMessageHelper(buyerMsg, true, "UTF-8");

        String buyerHtml = "<h3>Sifarişiniz Qəbul Olundu!</h3>"
                + "<p>Siz <b>" + computerName + "</b> məhsulu üçün sifariş verdiniz.</p>"
                + "<p>Qiymət: " + price + " AZN</p>"
                + "<p>Satıcı sizinlə tezliklə əlaqə saxlayacaq. Bizi seçdiyiniz üçün təşəkkürlər!</p>";

        buyerHelper.setTo(buyerEmail);
        buyerHelper.setSubject("Sifariş Təsdiqi - TechStore");
        buyerHelper.setText(buyerHtml, true);
        mailSender.send(buyerMsg);

        MimeMessage sellerMsg = mailSender.createMimeMessage();
        MimeMessageHelper sellerHelper = new MimeMessageHelper(sellerMsg, true, "UTF-8");

        // Nömrənin son 2 rəqəmini saxlayıb qalanını ulduzlayaq
        String maskedPhone = buyerPhone.substring(0, buyerPhone.length() - 2) + "**";

        String sellerHtml = "<div style='border:2px solid #58a6ff; padding:20px;'>"
                + "<h2>Məhsulunuz Satıldı!</h2>"
                + "<p>Alıcının nömrəsi: <b>" + maskedPhone + "</b></p>"
                + "<p>Nömrəni tam görmək üçün 2 AZN ödəniş edib qəbzi bura atın.</p>"
                + "</div>";

        sellerHelper.setTo(sellerEmail);
        sellerHelper.setSubject("Məhsulunuz Satıldı!");
        sellerHelper.setText(sellerHtml, true);
        mailSender.send(sellerMsg);
        MimeMessage adminMsg = mailSender.createMimeMessage();
        MimeMessageHelper adminHelper = new MimeMessageHelper(adminMsg, true, "UTF-8");
        adminHelper.setTo("sabirmemmedli21152014@gmail.com");
        adminHelper.setSubject("YENİ ÖDƏNİŞ GÖZLƏNİLİR");
        adminHelper.setText("Satıcı: " + sellerEmail + " nömrəni almaq üçün 2 AZN ödəyəcək. Alıcı nömrəsi: " + buyerPhone, false);
        mailSender.send(adminMsg);
    }
}