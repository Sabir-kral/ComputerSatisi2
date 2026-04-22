package az.computer.demo.Mapper;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.LogEntity;
import az.computer.demo.Response.ComputerResponse;
import az.computer.demo.Response.LogResponse;

import java.util.List;
import java.util.stream.Collectors;

public class LogMapper {
    public static LogResponse toDTO(LogEntity entity){
        LogResponse response = new LogResponse();
        response.setId(entity.getId());
        response.setMessage(entity.getMessage());
        response.setLogType(entity.getLogType());

        return response;
    }

    public static List<LogResponse> toDTOList(List<LogEntity> entities){
        return entities.stream().map(LogMapper::toDTO).collect(Collectors.toList());
    }
}
