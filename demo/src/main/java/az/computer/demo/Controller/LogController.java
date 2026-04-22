package az.computer.demo.Controller;

import az.computer.demo.Response.LogResponse;
import az.computer.demo.Service.LogService;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogController {
    private final LogService logService;

    @GetMapping("/v1")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<LogResponse> getAll(){
        return logService.getAll();
    }
}
