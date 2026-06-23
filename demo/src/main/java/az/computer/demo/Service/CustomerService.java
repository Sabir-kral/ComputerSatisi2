package az.computer.demo.Service;

import az.computer.demo.Entity.*;
import az.computer.demo.Mapper.*;
import az.computer.demo.Repo.*;
import az.computer.demo.Request.CustomerRequest;
import az.computer.demo.Response.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final CustomerRepo customerRepo;
    private final RoleRepo roleRepo;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MailService mailService;
    private final ComputerRepo computerRepo;
    private final LogService logService;

    @Transactional
    public MessageResponse register(CustomerRequest request) throws MessagingException {
        userService.isUserExists(request.getEmail());
        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsVerified(false);

        CustomerEntity customer = new CustomerEntity();
        customer.setName(request.getName());
        customer.setSurname(request.getSurname());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setSellingComputers(new ArrayList<>());
        customer.setBoughtComputers(new ArrayList<>());
        customer.setUser(user);

        userRepo.save(user);
        roleRepo.assignCustomerRoles(user.getId());
        customerRepo.save(customer);

        String code = generateCode();
        EmailVerificationEntity entity = new EmailVerificationEntity();
        entity.setToken(code);
        entity.setUser(user);
        entity.setEmail(request.getEmail());
        entity.setExpirationDate(LocalDateTime.now().plusMinutes(2));
        emailVerificationRepository.save(entity);

        mailService.verifyEmail(request.getEmail(), code);
        logService.add("Customer registered: " + customer.getEmail(), "REGISTERED");
        return new MessageResponse("Customer registered successfully");
    }

    @Transactional(readOnly = true)
    public List<ComputerResponse> getAllBought(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found"));
        
        // LAZY yükənmənin qarşısını almaq üçün size-ı çağırırıq
        int size = customer.getBoughtComputers().size();
        return ComputerMapper.toDTOList(customer.getBoughtComputers());
    }

    @Transactional
    public MessageResponse buyComputer(Long computerId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity buyer = customerRepo.findByEmail(email).orElseThrow();
        ComputerEntity computer = computerRepo.findById(computerId).orElseThrow();

        if (buyer.getBoughtComputers().contains(computer)) throw new RuntimeException("Artıq almısınız");

        if (computer.getSellers() != null) {
            for (CustomerEntity seller : new ArrayList<>(computer.getSellers())) {
                seller.getSellingComputers().remove(computer);
                customerRepo.save(seller);
            }
        }
        buyer.getBoughtComputers().add(computer);
        customerRepo.save(buyer);
        return new MessageResponse("Computer bought successfully");
    }

    private String generateCode() {
        return String.valueOf(new SecureRandom().nextInt(900000) + 100000);
    }
}