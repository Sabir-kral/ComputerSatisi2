package az.computer.demo.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    /**
     * E-poçt Təsdiqləmə (Verify Email) - Modern TechStore Dizaynı
     */
    public void verifyEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String htmlContent =
                "<div style='background-color: #0d1117; padding: 40px 20px; font-family: \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif; text-align: center; color: #c9d1d9;'>"
                        + "<div style='max-width: 500px; margin: 0 auto; background: #161b22; border: 1px solid #30363d; border-radius: 16px; padding: 30px; box-shadow: 0 10px 25px rgba(0,0,0,0.5);'>"
                        + "  <div style='margin-bottom: 20px;'>"
                        + "    <span style='font-size: 28px; font-weight: 800; background: linear-gradient(135deg, #7928CA, #FF0080); -webkit-background-clip: text; -webkit-text-fill-color: transparent;'>TECHSTORE</span>"
                        + "  </div>"
                        + "  <h2 style='color: #ffffff; font-size: 22px; margin-bottom: 10px;'>Hesabınızı Təsdiqləyin</h2>"
                        + "  <p style='color: #8b949e; font-size: 14px; margin-bottom: 25px;'>Qeydiyyatı tamamlamaq üçün aşağıdakı təsdiq kodunu istifadə edin:</p>"
                        + "  <div style='background: #21262d; border: 1px dashed #58a6ff; border-radius: 12px; padding: 15px; display: inline-block; margin-bottom: 20px;'>"
                        + "    <span style='font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #58a6ff;'>" + token + "</span>"
                        + "  </div>"
                        + "  <p style='color: #da3633; font-size: 12px; font-weight: 600;'>⏱ Kodun qüvvədə olma müddəti: 2 dəqiqə</p>"
                        + "  <hr style='border: 0; border-top: 1px solid #30363d; margin: 25px 0;'/>"
                        + "  <p style='font-size: 11px; color: #484f58;'>Əgər bu sorğunu siz etmədinizsə, bu e-poçtu nəzərə almayın.</p>"
                        + "</div>"
                        + "</div>";

        helper.setTo(email);
        helper.setSubject("🔒 TechStore - Hesab Təsdiqləmə Kodu");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * 1. ALICIYA GÖNDƏRİLƏN MAİL
     * Kart nömrəsi və ödəniş təlimatı ilə
     */
    public void sendBuyerOrderNotification(String buyerEmail, String computerName, double price, String bankCardNumber) throws MessagingException {
        MimeMessage buyerMsg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(buyerMsg, true, "UTF-8");

        String buyerHtml =
                "<div style='font-family: Arial, sans-serif; background-color: #f4f6f9; padding: 25px; color: #333;'>"
                        + "<div style='max-width: 550px; margin: 0 auto; background: #ffffff; border-radius: 10px; padding: 25px; border-top: 5px solid #2ecc71; box-shadow: 0 4px 10px rgba(0,0,0,0.05);'>"
                        + "  <h2 style='color: #2c3e50; margin-top: 0;'>🛒 Sifarişiniz Qəbul Olundu!</h2>"
                        + "  <p>Hörmətli müştəri, <b>" + computerName + "</b> məhsulu üçün sifarişiniz uğurla yaradıldı.</p>"
                        + "  <div style='background: #eef9f1; padding: 15px; border-left: 4px solid #2ecc71; margin: 20px 0; border-radius: 4px;'>"
                        + "    <p style='margin: 5px 0;'><b>Ödəniləcək Məbləğ:</b> <span style='font-size: 18px; color: #27ae60; font-weight: bold;'>" + price + " AZN</span></p>"
                        + "    <p style='margin: 5px 0;'><b>Ödəniş Ediləcək Kart:</b> <span style='font-size: 16px; font-weight: bold; font-family: monospace;'>" + bankCardNumber + "</span></p>"
                        + "  </div>"
                        + "  <p style='line-height: 1.6;'>Sifarişinizin təsdiqlənməsi üçün zəhmət olmasa yuxarıdakı kart nömrəsinə <b>" + price + " AZN</b> keçirib, ödəniş çəkini (qəbzini) gmaildən göndərin.</p>"
                        + "  <p style='font-size: 13px; color: #7f8c8d; margin-top: 20px;'>Bizi seçdiyiniz üçün təşəkkür edirik!<br/><b>TechStore Komandası</b></p>"
                        + "</div>"
                        + "</div>";

        helper.setTo(buyerEmail);
        helper.setSubject("Sifariş Təsdiqi və Ödəniş Məlumatı - TechStore");
        helper.setText(buyerHtml, true);

        mailSender.send(buyerMsg);
    }

    /**
     * 2. ADMİNE (SƏNƏ) GÖNDƏRİLƏN MAİL
     * Alıcının nömrəsi və çək gözləntisi ilə
     */
    public void sendAdminOrderAlert(String buyerEmail, String buyerPhone, String pcName, double price) throws MessagingException {
        MimeMessage adminMsg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(adminMsg, true, "UTF-8");

        String htmlContent =
                "<div style='font-family: Arial, sans-serif; background-color: #fff5f5; padding: 25px; color: #333;'>"
                        + "<div style='max-width: 550px; margin: 0 auto; background: #ffffff; border: 1px solid #feb2b2; border-radius: 10px; padding: 25px; box-shadow: 0 4px 10px rgba(0,0,0,0.05);'>"
                        + "  <h2 style='color: #e53e3e; margin-top: 0;'>🚨 YENİ SİFARİŞ ALINDI</h2>"
                        + "  <p>Sistemdə yeni bir sifariş yaradıldı və alıcıya ödəniş məlumatları göndərildi.</p>"
                        + "  <hr style='border: 0; border-top: 1px solid #edf2f7; margin: 15px 0;'/>"
                        + "  <p><b>Məhsul:</b> " + pcName + "</p>"
                        + "  <p><b>Qiymət:</b> " + price + " AZN</p>"
                        + "  <p><b>Alıcı Email:</b> " + buyerEmail + "</p>"
                        + "  <div style='background: #edf2f7; padding: 12px; border-radius: 6px; margin: 15px 0;'>"
                        + "    <p style='margin: 0; font-size: 16px;'><b>Alıcının Əlaqə Nömrəsi:</b> <span style='color: #2b6cb0; font-weight: bold;'>" + buyerPhone + "</span></p>"
                        + "  </div>"
                        + "  <p style='color: #c53030; font-size: 13px; font-weight: bold;'>⚠️ QEYD: Alıcı ödəniş çəkini göndərdikdən sonra onunla bu nömrə üzərindən əlaqə saxlayın.</p>"
                        + "</div>"
                        + "</div>";

        helper.setTo("sabirmemmedli21152014@gmail.com");
        helper.setSubject("🔔 Yeni Sifariş Bildirişi - " + pcName);
        helper.setText(htmlContent, true);

        mailSender.send(adminMsg);
    }
}