package az.computer.demo.Service;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Entity.ImageEntity;
import az.computer.demo.Mapper.ComputerMapper;
import az.computer.demo.Repo.ComputerRepo;
import az.computer.demo.Repo.CustomerRepo;
import az.computer.demo.Request.ComputerRequest;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.MessageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComputerService {

    private final ComputerRepo computerRepo;
    private final CustomerRepo customerRepo;

    @Transactional
    public ComputerResponse add(ComputerRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerEntity customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ComputerEntity computer = new ComputerEntity();
        computer.setName(request.getName());
        computer.setDescription(request.getDescription());
        computer.setPrice(request.getPrice());
        computer.setImages(new ArrayList<>());

        // Şəkilləri əlavə edirik
        if (request.getImageLinks() != null) {
            for (String link : request.getImageLinks()) {
                ImageEntity image = new ImageEntity();
                image.setImageUrl(link);
                image.setComputer(computer);
                computer.getImages().add(image);
            }
        }

        computerRepo.save(computer);
        customer.getSellingComputers().add(computer);
        customerRepo.save(customer);

        return ComputerMapper.toDTO(computer);
    }

    public MessageResponse update(Long id, ComputerRequest request){
        ComputerEntity computer = computerRepo.findById(id).orElseThrow(()->new RuntimeException("Not Found"));
        computer.setName(request.getName());
        computer.setDescription(request.getDescription());
        computer.setPrice(request.getPrice());

        // Update zamanı şəkilləri də yeniləmək olar (isteğe bağlı)
        computerRepo.save(computer);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("Updated successfully");
        return messageResponse;
    }

    public ComputerResponse id(Long id){
        ComputerEntity computer = computerRepo.findById(id).orElseThrow(()->new RuntimeException("Not Found"));
        return ComputerMapper.toDTO(computer);
    }

    @Transactional
    public void delete(Long computerId) {
        ComputerEntity computer = computerRepo.findById(computerId)
                .orElseThrow(() -> new RuntimeException("Computer not found"));

        List<CustomerEntity> customers = customerRepo.findAll();

        for (CustomerEntity customer : customers) {
            customer.getBoughtComputers().remove(computer);
            customer.getSellingComputers().remove(computer);
        }

        customerRepo.saveAll(customers);
        computerRepo.delete(computer);
    }
}