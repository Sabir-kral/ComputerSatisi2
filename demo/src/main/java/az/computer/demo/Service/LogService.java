package az.computer.demo.Service;

import az.computer.demo.Entity.LogEntity;
import az.computer.demo.Mapper.LogMapper;
import az.computer.demo.Repo.LogRepo;
import az.computer.demo.Request.LogRequest;
import az.computer.demo.Response.LogResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepo logRepo;

    public LogResponse add(String message,String logType){
        LogEntity logEntity = new LogEntity();
        logEntity.setMessage(message);
        logEntity.setLogType(logType);
        logRepo.save(logEntity);
        return LogMapper.toDTO(logEntity);
    }
    public List<LogResponse> getAll(){
        return LogMapper.toDTOList(logRepo.findAll());
    }
}
