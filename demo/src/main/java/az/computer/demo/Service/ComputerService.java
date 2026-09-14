package az.computer.demo.Service;

import az.computer.demo.Entity.*;
import az.computer.demo.Mapper.ComputerMapper;
import az.computer.demo.Repo.*;
import az.computer.demo.Request.ComputerRequest;
import az.computer.demo.Response.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ComputerService {
    private final ComputerRepo computerRepo;
    private final CustomerRepo customerRepo;

    @Transactional
    public ComputerResponse add(ComputerRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomerEntity customer = customerRepo.findByEmail(email).orElseThrow();

        ComputerEntity computer = new ComputerEntity();
        computer.setName(request.getName());
        computer.setDescription(request.getDescription());
        computer.setPrice(request.getPrice());
        computer.setImages(new ArrayList<>());

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

    @Transactional
    public MessageResponse update(Long id, ComputerRequest request){
        ComputerEntity computer = computerRepo.findById(id).orElseThrow();
        computer.setName(request.getName());
        computer.setDescription(request.getDescription());
        computer.setPrice(request.getPrice());
        computerRepo.save(computer);
        return new MessageResponse("Updated successfully");
    }
}