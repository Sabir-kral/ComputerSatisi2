package az.computer.demo.Service;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Entity.UserEntity;
import az.computer.demo.Mapper.ComputerMapper;
import az.computer.demo.Repo.ComputerRepo;
import az.computer.demo.Repo.CustomerRepo;
import az.computer.demo.Repo.RoleRepo;
import az.computer.demo.Repo.UserRepo;
import az.computer.demo.Request.AdminUserUpdateRequest;
import az.computer.demo.Response.AdminUserResponse;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.MessageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepo customerRepo;
    private final UserRepo userRepo;
    private final ComputerRepo computerRepo;
    private final RoleRepo roleRepo;

    public List<AdminUserResponse> getAllUsers() {
        return customerRepo.findAll().stream().map(this::toResponse).toList();
    }

    public AdminUserResponse getUser(Long id) {
        CustomerEntity customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));
        return toResponse(customer);
    }

    @Transactional
    public MessageResponse updateUser(Long id, AdminUserUpdateRequest request) {
        CustomerEntity customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));
        UserEntity user = customer.getUser();

        if (request.getName() != null) customer.setName(request.getName());
        if (request.getSurname() != null) customer.setSurname(request.getSurname());
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
            if (user != null) user.setEmail(request.getEmail());
        }

        customerRepo.save(customer);
        if (user != null) userRepo.save(user);

        MessageResponse response = new MessageResponse();
        response.setMessage("İstifadəçi yeniləndi");
        return response;
    }

    @Transactional
    public MessageResponse banUser(Long id) {
        CustomerEntity customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));
        UserEntity user = customer.getUser();
        if (user == null) throw new RuntimeException("İstifadəçi hesabı tapılmadı");
        user.setBanned(true);
        userRepo.save(user);

        MessageResponse response = new MessageResponse();
        response.setMessage("İstifadəçi bloklandı");
        return response;
    }

    @Transactional
    public MessageResponse unbanUser(Long id) {
        CustomerEntity customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));
        UserEntity user = customer.getUser();
        if (user == null) throw new RuntimeException("İstifadəçi hesabı tapılmadı");
        user.setBanned(false);
        userRepo.save(user);

        MessageResponse response = new MessageResponse();
        response.setMessage("Blok qaldırıldı");
        return response;
    }

    public List<ComputerResponse> getAllComputers() {
        return ComputerMapper.toDTOList(computerRepo.findAll());
    }

    @Transactional
    public MessageResponse promoteToAdmin(String email) {
        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bu email ilə qeydiyyatdan keçmiş istifadəçi tapılmadı"));
        roleRepo.assignAdminRoles(user.getId());

        MessageResponse response = new MessageResponse();
        response.setMessage("İstifadəçi admin edildi");
        return response;
    }

    private AdminUserResponse toResponse(CustomerEntity customer) {
        AdminUserResponse response = new AdminUserResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setSurname(customer.getSurname());
        response.setEmail(customer.getEmail());
        response.setBanned(customer.getUser() != null && Boolean.TRUE.equals(customer.getUser().getBanned()));
        return response;
    }
}