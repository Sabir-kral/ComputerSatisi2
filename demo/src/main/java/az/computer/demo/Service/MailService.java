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
}