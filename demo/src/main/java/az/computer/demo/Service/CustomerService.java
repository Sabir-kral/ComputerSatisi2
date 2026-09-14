package az.computer.demo.Service;

import az.computer.demo.Entity.*;
import az.computer.demo.Mapper.ComputerMapper;
import az.computer.demo.Repo.*;
import az.computer.demo.Request.CustomerRequest;
import az.computer.demo.Response.*;
import jakarta.mail.MessagingException;
import org.springframework.transaction.annotation.Transactional;
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

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Customer registered successfully");
        return messageResponse;
    }

    @Transactional
    public MessageResponse updateCustomerProfile(CustomerRequest request, String email) {
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        UserEntity userEntity = customer.getUser();

        customer.setName(request.getName());
        customer.setSurname(request.getSurname());
        customer.setEmail(request.getEmail());

        if(request.getPassword() != null && !request.getPassword().isEmpty()){
            String encodedPass = passwordEncoder.encode(request.getPassword());
            customer.setPassword(encodedPass);
            userEntity.setPassword(encodedPass);
        }

        userEntity.setEmail(request.getEmail());

        customerRepo.save(customer);
        userRepo.save(userEntity);

        logService.add("Customer updated with email: " + customer.getEmail(), "CUSTOMER_UPDATED");

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Customer Updated");
        return messageResponse;
    }

    @Transactional
    public void delete() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customerRepo.delete(customer);
        logService.add("Customer deleted: " + email, "CUSTOMER_DELETED");
    }

    @Transactional(readOnly = true)
    public CustomerResponse profile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setSurname(customer.getSurname());
        response.setEmail(customer.getEmail());
        return response;
    }

    @Transactional
    public MessageResponse contactSeller(Long computerId, String phone) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity buyer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ComputerEntity computer = computerRepo.findById(computerId)
                .orElseThrow(() -> new RuntimeException("Computer not found"));

        String bankCardNumber = "4169 7388 XXXX XXXX";

        mailService.sendBuyerOrderNotification(
                buyer.getEmail(),
                computer.getName(),
                computer.getPrice(),
                bankCardNumber
        );

        mailService.sendAdminOrderAlert(
                buyer.getEmail(),
                phone,
                computer.getName(),
                computer.getPrice()
        );

        logService.add("Customer " + buyer.getEmail() + " ordered PC ID: " + computerId, "CUSTOMER_ORDERED");

        MessageResponse response = new MessageResponse();
        response.setMessage("Sifarişiniz qəbul olundu. Ödəniş təlimatı e-poçt ünvanınıza göndərildi.");
        return response;
    }

    @Transactional(readOnly = true)
    public List<ComputerResponse> getAllBought() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found"));

        int size = customer.getBoughtComputers().size();
        return ComputerMapper.toDTOList(customer.getBoughtComputers());
    }

    @Transactional(readOnly = true)
    public List<ComputerResponse> getAll() {
        List<ComputerEntity> computers = computerRepo.findAll();
        return ComputerMapper.toDTOList(computers);
    }

    @Transactional(readOnly = true)
    public List<ComputerResponse> getSelling() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found"));

        return ComputerMapper.toDTOList(customer.getSellingComputers());
    }

    @Transactional
    public MessageResponse buyComputer(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity buyer = customerRepo.findByEmail(email).orElseThrow();
        ComputerEntity computer = computerRepo.findById(id).orElseThrow();

        if (buyer.getBoughtComputers().contains(computer)) throw new RuntimeException("Artıq almısınız");

        if (computer.getSellers() != null) {
            for (CustomerEntity seller : new ArrayList<>(computer.getSellers())) {
                seller.getSellingComputers().remove(computer);
                customerRepo.save(seller);
            }
        }

        buyer.getBoughtComputers().add(computer);
        customerRepo.save(buyer);

        logService.add("Customer " + buyer.getEmail() + " bought PC ID: " + id, "CUSTOMER_BOUGHT");

        MessageResponse response = new MessageResponse();
        response.setMessage("Computer bought successfully");
        return response;
    }

    private String generateCode() {
        return String.valueOf(new SecureRandom().nextInt(900000) + 100000);
    }
}