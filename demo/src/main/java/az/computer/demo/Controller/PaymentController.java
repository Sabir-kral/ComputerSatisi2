package az.computer.demo.Controller;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.CustomerEntity;
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
        // 1. Alıcının emailini SecurityContext-den tapırıq
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Her bir kompüter ID-si üçün dövr işe düşür
        for (Long cpId : request.getComputerIds()) {
            ComputerEntity computer = computerRepo.findById(cpId)
                    .orElseThrow(() -> new RuntimeException("Kompüter tapılmadı ID: " + cpId));

            if (computer.getSellers() != null && !computer.getSellers().isEmpty()) {
                CustomerEntity seller = computer.getSellers().get(0);
                String sellerEmail = seller.getEmail();

                // 3. Alıcıya ve Satıcıya standart bildirişler gedir
                mailService.sendOrderNotifications(
                        buyerEmail,
                        sellerEmail,
                        computer.getName(),
                        computer.getPrice(),
                        request.getPhone()
                );

                // 4. ADMİNE (SENE) GEDEN ÖZEL BİLDİRİŞ
                // Bu metodu MailService-de aşağıda teyin edirik
                mailService.sendAdminOrderAlert(
                        sellerEmail,
                        buyerEmail,
                        request.getPhone(),
                        computer.getName(),
                        computer.getPrice()
                );
            }
        }

        return ResponseEntity.ok("Sifariş tamamlandı. Admin ve terefler melumatlandırıldı.");
    }
}