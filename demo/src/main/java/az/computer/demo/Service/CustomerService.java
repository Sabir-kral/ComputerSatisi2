package az.computer.demo.Service;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Entity.EmailVerificationEntity;
import az.computer.demo.Entity.UserEntity;
import az.computer.demo.Mapper.ComputerMapper;
import az.computer.demo.Mapper.CustomerMapper;
import az.computer.demo.Repo.*;
import az.computer.demo.Request.CustomerRequest;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.CustomerResponse;
import az.computer.demo.Response.MessageResponse;
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
        userService.isUserExists(request.getEmail()); // now safely deletes old unverified

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

        MessageResponse response = new MessageResponse();
        response.setMessage("Customer registered successfully");
        response.setIsVerified(false);
        logService.add("Customer registered with email: " + customer.getEmail(), "CUSTOMER_REGISTERED");

        return response;
    }

    public CustomerResponse profile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + username));

        CustomerResponse dto = CustomerMapper.toDTO(customer);
        // Satdığı kompüterləri göstəririk
        dto.setSellingComputers(ComputerMapper.toDTOList(customer.getSellingComputers()));
        return dto;
    }

    @Transactional
    public String buyComputer(Long computerId, String buyerPhone) {
        ComputerEntity computer = computerRepo.findById(computerId)
                .orElseThrow(() -> new RuntimeException("Kompüter tapılmadı!"));

        CustomerEntity seller = computer.getSellers().get(0);
        String buyerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            // MailService-ə nömrəni də göndəririk ki, yadda saxlasın və ya sənə xəbər versin
            mailService.sendOrderNotifications(
                    buyerEmail,
                    seller.getEmail(),
                    computer.getName(),
                    computer.getPrice(),
                    buyerPhone // Bu nömrəni metodun arqumentlərinə əlavə et
            );

            return "Uğurlu! Satıcıya bildiriş göndərildi.";
        } catch (MessagingException e) {
            return "Xəta baş verdi.";
        }
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

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Customer Updated");
        logService.add("Customer updated with email: "+customer.getEmail(),"CUSTOMER_UPDATED");
        return messageResponse;
    }

    private String generateCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            codeBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }

    @Transactional
    public void delete() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));

        UserEntity user = customer.getUser();
        roleRepo.deleteByUserId(user.getId());
        customerRepo.delete(customer);
        userRepo.delete(user);
        logService.add("Customer deleted with email: "+user.getEmail(),"CUSTOMER_DELETED");
    }

    public List<ComputerResponse> getAllBought(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email).orElseThrow(()->new RuntimeException("Not Found"));
        return ComputerMapper.toDTOList(customer.getBoughtComputers());
    }

    public List<ComputerResponse> getAll() {
        List<ComputerEntity> allComputers = computerRepo.findAll();
        List<ComputerEntity> availableComputers = allComputers.stream()
                .filter(c -> c.getSellers() != null && !c.getSellers().isEmpty())
                .toList();
        return ComputerMapper.toDTOList(availableComputers);
    }

    public List<ComputerResponse> getSelling() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Not Found"));
        return ComputerMapper.toDTOList(customer.getSellingComputers());
    }
}