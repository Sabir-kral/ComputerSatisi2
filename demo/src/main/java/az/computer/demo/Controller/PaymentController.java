package az.computer.demo.Controller;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Repo.ComputerRepo;
import az.computer.demo.Repo.CustomerRepo;
import az.computer.demo.Request.OrderRequest;
import az.computer.demo.Service.MailService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final ComputerRepo computerRepo;
    private final CustomerRepo customerRepo;
    private final MailService mailService;

    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<?> checkout(@RequestBody OrderRequest request) throws MessagingException {
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String bankCardNumber = "4613 8602 0120 5026";

        for (Long cpId : request.getComputerIds()) {
            ComputerEntity computer = computerRepo.findById(cpId)
                    .orElseThrow(() -> new RuntimeException("Kompüter tapılmadı ID: " + cpId));

            // 1. E-poçt bildirişlərini göndəririk
            mailService.sendBuyerOrderNotification(
                    buyerEmail,
                    computer.getName(),
                    computer.getPrice(),
                    bankCardNumber
            );

            mailService.sendAdminOrderAlert(
                    buyerEmail,
                    request.getPhone(),
                    computer.getName(),
                    computer.getPrice()
            );

            // 2. BAZADAN (DB) BİRDƏFƏLİK SİLMƏK ÜÇÜN ƏLAQƏLƏRİ TƏMİZLƏYİRİK:

            // Satıcıların siyahısından çıxarırıq (Many-to-Many əlaqəsini qırırıq)
            if (computer.getSellers() != null) {
                for (CustomerEntity seller : new ArrayList<>(computer.getSellers())) {
                    seller.getSellingComputers().remove(computer);
                    customerRepo.save(seller);
                }
                computer.getSellers().clear();
            }

            // Alıcıların siyahısından çıxarırıq (Many-to-Many əlaqəsini qırırıq)
            if (computer.getBuyers() != null) {
                for (CustomerEntity buyer : new ArrayList<>(computer.getBuyers())) {
                    buyer.getBoughtComputers().remove(computer);
                    customerRepo.save(buyer);
                }
                computer.getBuyers().clear();
            }

            // 3. Məhsulu bazadan (DB) tamamilə silirik
            computerRepo.delete(computer);
        }

        return ResponseEntity.ok("Sifariş tamamlandı.Zəhmət olmasa e-poçtunuzu yoxlayin");
    }
}