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
        dto.setSellingComputers(ComputerMapper.toDTOList(customer.getSellingComputers()));
        return dto;
    }

    @Transactional
    public MessageResponse buyComputer(Long computerId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity buyer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ComputerEntity computer = computerRepo.findById(computerId)
                .orElseThrow(() -> new RuntimeException("Computer not found"));

        if (buyer.getBoughtComputers().contains(computer)) {
            throw new RuntimeException("Siz bu kompüteri artıq almısınız.");
        }

        // Satıcıların vitrinindən (satdıqları siyahısından) çıxarırıq
        if (computer.getSellers() != null && !computer.getSellers().isEmpty()) {
            for (CustomerEntity seller : new ArrayList<>(computer.getSellers())) {
                seller.getSellingComputers().remove(computer);
                customerRepo.save(seller);
            }
        }

        // Alıcının alınanlar siyahısına əlavə edirik
        buyer.getBoughtComputers().add(computer);
        customerRepo.save(buyer);

        logService.add("Customer " + buyer.getEmail() + " bought PC ID: " + computerId, "CUSTOMER_BOUGHT");

        MessageResponse response = new MessageResponse();
        response.setMessage("Computer bought successfully");
        return response;
    }

    /**
     * YENİLƏNMİŞ SİFARİŞ METODU
     * Artıq alıcıya ödəniş təlimatı və kart nömrəsini, admine isə alıcının məlumatlarını göndərir.
     */
    public MessageResponse contactSeller(Long computerId, String phone) throws MessagingException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity buyer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ComputerEntity computer = computerRepo.findById(computerId)
                .orElseThrow(() -> new RuntimeException("Computer not found"));

        // Öz kart nömrəni bura əlavə et:
        String bankCardNumber = "4169 7388 XXXX XXXX";

        // 1. Alıcıya ödəniş təlimatı və kart nömrəsini göndər
        mailService.sendBuyerOrderNotification(
                buyer.getEmail(),
                computer.getName(),
                computer.getPrice(),
                bankCardNumber
        );

        // 2. Admine (Sənə) alıcının əlaqə nömrəsi və məhsul haqqında məlumat göndər
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