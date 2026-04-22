package az.computer.demo.Controller;

import az.computer.demo.Request.ComputerRequest;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.MessageResponse;
import az.computer.demo.Service.ComputerService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.DeclareWarning;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/computers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ComputerController {
    private final ComputerService computerService;

    @PostMapping("/add")
    public ComputerResponse add(@RequestBody ComputerRequest request){
        return computerService.add(request);
    }

    @PutMapping("/{id}")
    public MessageResponse update(@PathVariable Long id,@RequestBody ComputerRequest request){
        return computerService.update(id,request);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        computerService.delete(id);
    }

    @GetMapping("/{id}")
    public ComputerResponse Id(@PathVariable Long id){
        return computerService.id(id);
    }
}
