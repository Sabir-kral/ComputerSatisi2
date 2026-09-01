package az.computer.demo.Controller;

import az.computer.demo.Request.AdminUserUpdateRequest;
import az.computer.demo.Response.AdminUserResponse;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.MessageResponse;
import az.computer.demo.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/check")
    public MessageResponse check() {
        MessageResponse response = new MessageResponse();
        response.setMessage("OK");
        return response;
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public AdminUserResponse getUser(@PathVariable Long id) {
        return adminService.getUser(id);
    }

    @PutMapping("/users/{id}")
    public MessageResponse updateUser(@PathVariable Long id, @RequestBody AdminUserUpdateRequest request) {
        return adminService.updateUser(id, request);
    }

    @PutMapping("/users/{id}/ban")
    public MessageResponse banUser(@PathVariable Long id) {
        return adminService.banUser(id);
    }

    @PutMapping("/users/{id}/unban")
    public MessageResponse unbanUser(@PathVariable Long id) {
        return adminService.unbanUser(id);
    }

    @GetMapping("/computers")
    public List<ComputerResponse> getAllComputers() {
        return adminService.getAllComputers();
    }

    @PostMapping("/promote")
    public MessageResponse promote(@RequestParam String email) {
        return adminService.promoteToAdmin(email);
    }
}