package az.computer.demo.Controller;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Repo.ComputerRepo;
import az.computer.demo.Request.OrderRequest;
import az.computer.demo.Service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final ComputerRepo computerRepo;
    private final MailService mailService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody OrderRequest request) throws MessagingException {
        // 1. Alıcının emailini SecurityContext-dən alırıq
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Kart nömrəni bura əlavə et:
        String bankCardNumber = "4613 8602 0120 5026";

        // 2. Hər bir kompüter ID-si üçün dövr işə düşür
        for (Long cpId : request.getComputerIds()) {
            ComputerEntity computer = computerRepo.findById(cpId)
                    .orElseThrow(() -> new RuntimeException("Kompüter tapılmadı ID: " + cpId));

            // 3. ALICIYA GÖNDƏRİLƏN MAİL (Ödəniş təlimatı və kart nömrəsi)
            mailService.sendBuyerOrderNotification(
                    buyerEmail,
                    computer.getName(),
                    computer.getPrice(),
                    bankCardNumber
            );

            // 4. ADMİNE (SƏNƏ) GÖNDƏRİLƏN ÖZƏL BİLDİRİŞ (Alıcının tam nömrəsi ilə)
            mailService.sendAdminOrderAlert(
                    buyerEmail,
                    request.getPhone(),
                    computer.getName(),
                    computer.getPrice()
            );
        }

        return ResponseEntity.ok("Sifariş tamamlandı.Zəhmət olmasa e-poçtunuza baxin.");
    }
}