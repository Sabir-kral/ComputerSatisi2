package az.computer.demo.Controller;

import az.computer.demo.Request.AdminRequest;
import az.computer.demo.Response.AdminResponse;
import az.computer.demo.Response.MessageResponse;
import az.computer.demo.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admins")
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    public MessageResponse register(@RequestBody AdminRequest request){
        return adminService.register(request);
    }
    @PutMapping
    public MessageResponse update(@RequestBody AdminRequest request){
        return adminService.update(request);
    }
    @DeleteMapping
    public void delete(){
        adminService.delete();
    }
}
