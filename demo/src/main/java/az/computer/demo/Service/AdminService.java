package az.computer.demo.Service;

import az.computer.demo.Entity.AdminEntity;
import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Entity.UserEntity;
import az.computer.demo.Repo.AdminRepo;
import az.computer.demo.Repo.RoleRepo;
import az.computer.demo.Repo.UserRepo;
import az.computer.demo.Request.AdminRequest;
import az.computer.demo.Response.AdminResponse;
import az.computer.demo.Response.MessageResponse;
import jakarta.mail.MessageAware;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepo repo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    public MessageResponse register(AdminRequest request){
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));

        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setName(request.getName());
        adminEntity.setEmail(request.getEmail());
        adminEntity.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(userEntity);
        repo.save(adminEntity);
        roleRepo.assignAdminRoles(userEntity.getId());
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Admin registered successfully");
        return messageResponse;
    }

    public MessageResponse update(AdminRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AdminEntity adminEntity = repo.findByEmail(email).orElseThrow(()->new RuntimeException("Not Found"));
        UserEntity user = adminEntity.getUser();
        adminEntity.setName(request.getName());
        adminEntity.setEmail(request.getEmail());
        adminEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Admin updated successfully");
        return messageResponse;
    }

    @Transactional
    public void delete() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AdminEntity admin = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));


        UserEntity user = admin.getUser();

        roleRepo.deleteByUserId(user.getId());

        repo.delete(admin);

        userRepo.delete(user);
    }
}
