package az.computer.demo.Controller;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Request.CustomerRequest;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.CustomerResponse;
import az.computer.demo.Response.MessageResponse;
import az.computer.demo.Service.ComputerService;
import az.computer.demo.Service.CustomerService;
import az.computer.demo.Service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CustomerController {

    private final CustomerService service;
    private final MailService mailService;
    private final ComputerService computerService;

    @PostMapping
    public MessageResponse register(@RequestBody @Valid CustomerRequest studentRequest) throws MessagingException {
        return service.register(studentRequest);
    }

    @PutMapping("/profile")
    public MessageResponse update(@RequestParam String email, @RequestBody CustomerRequest request){
        return service.updateCustomerProfile(request,email);
    }

    @DeleteMapping("/delete")
    public void delete(){
        service.delete();
    }

    @GetMapping("/profile")
    public CustomerResponse profile(){
        return service.profile();
    }
    @GetMapping("/v1")
    @Operation(summary = "Get bought pc")
    public List<ComputerResponse> getAllBought(){
        return service.getAllBought();
    }
    @GetMapping("/v2")
    @Operation(summary = "Get all pc")
    public List<ComputerResponse> getAll(){
        return service.getAll();
    }
    @GetMapping("/selling")
    @Operation(summary = "Get selling computers")
    public List<ComputerResponse> getSelling(){
        return service.getSelling();
    }



    @PostMapping("/buy")
    public ResponseEntity<String> buyMultiple(@RequestParam List<Long> ids, @RequestParam String phone) {
        for (Long id : ids) {
            service.buyComputer(id, phone);
        }
        return ResponseEntity.ok("Bütün sifarişlər üçün maillər göndərildi!");
    }
}