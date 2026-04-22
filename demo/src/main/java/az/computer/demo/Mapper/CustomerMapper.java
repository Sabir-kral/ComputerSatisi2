package az.computer.demo.Mapper;

import az.computer.demo.Entity.CustomerEntity;
import az.computer.demo.Response.CustomerResponse;

import java.util.stream.Collectors;

public class CustomerMapper {

    public static CustomerResponse toDTO(CustomerEntity entity) {
        if (entity == null) return null;

        CustomerResponse dto = new CustomerResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setEmail(entity.getEmail());

        // Satılan kompüterləri çevir
        if (entity.getSellingComputers() != null) {
            dto.setSellingComputers(ComputerMapper.toDTOList(entity.getSellingComputers()));
        }

        // Alınan kompüterləri çevir
        if (entity.getBoughtComputers() != null) {
            dto.setBoughtComputers(ComputerMapper.toDTOList(entity.getBoughtComputers()));
        }

        return dto;
    }
}