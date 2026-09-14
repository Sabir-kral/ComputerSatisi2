package az.computer.demo.Service;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Mapper.ComputerMapper;
import az.computer.demo.Repo.ComputerRepo;
import az.computer.demo.Service.LogService;
import az.computer.demo.Request.ComputerRequest;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComputerService {
    private final ComputerRepo computerRepo;
    private final LogService logService;

    @Transactional
    public ComputerResponse add(ComputerRequest request) {
        ComputerEntity computer = new ComputerEntity();
        computer.setName(request.getName());
        computer.setDescription(request.getDescription());
        computer.setPrice(request.getPrice());
        // Əgər request-də digər sahələr (məsələn, şəkil linkləri) varsa buraya əlavə edə bilərsiniz

        ComputerEntity savedComputer = computerRepo.save(computer);
        logService.add("Computer added: " + savedComputer.getName(), "COMPUTER_ADDED");

        return ComputerMapper.toDTO(savedComputer);
    }

    @Transactional
    public MessageResponse update(Long id, ComputerRequest request) {
        ComputerEntity computer = computerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Computer not found with id: " + id));

        computer.setName(request.getName());
        computer.setDescription(request.getDescription());
        computer.setPrice(request.getPrice());

        computerRepo.save(computer);
        logService.add("Computer updated ID: " + id, "COMPUTER_UPDATED");

        MessageResponse response = new MessageResponse();
        response.setMessage("Computer updated successfully");
        return response;
    }

    @Transactional
    public void delete(Long id) {
        ComputerEntity computer = computerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Computer not found with id: " + id));

        computerRepo.delete(computer);
        logService.add("Computer deleted ID: " + id, "COMPUTER_DELETED");
    }

    @Transactional(readOnly = true)
    public ComputerResponse id(Long id) {
        ComputerEntity computer = computerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Computer not found with id: " + id));

        return ComputerMapper.toDTO(computer);
    }
}