package az.computer.demo.Mapper;

import az.computer.demo.Entity.CartEntity;
import az.computer.demo.Response.CartComputerResponse;
import az.computer.demo.Response.CartResponse;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartResponse toDTO(CartEntity entity) {
        CartResponse response = new CartResponse();
        response.setId(entity.getId());
        response.setQuantity(entity.getQuantity());

        if (entity.getComputer() != null) {
            CartComputerResponse computerResponse = new CartComputerResponse();
            computerResponse.setId(entity.getComputer().getId());
            computerResponse.setName(entity.getComputer().getName());
            computerResponse.setPrice(entity.getComputer().getPrice());
            response.setComputer(computerResponse);
        }

        return response;
    }

    public static List<CartResponse> toDTOList(List<CartEntity> entities) {
        return entities.stream().map(CartMapper::toDTO).collect(Collectors.toList());
    }
}