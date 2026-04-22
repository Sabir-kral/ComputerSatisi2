package az.computer.demo.Mapper;

import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.ImageEntity;
import az.computer.demo.Response.ComputerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComputerMapper {

    public static ComputerResponse toDTO(ComputerEntity entity) {
        if (entity == null) return null;

        ComputerResponse response = new ComputerResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPrice(entity.getPrice());

        // Bura diqqət: Entity-dəki Image obyektlərini String linklərinə çeviririk
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            List<String> links = entity.getImages().stream()
                    .map(ImageEntity::getImageUrl)
                    .collect(Collectors.toList());
            response.setImageLinks(links);
        } else {
            response.setImageLinks(new ArrayList<>());
        }

        // Əgər satıcısı varsa deməli hələ də satışdadır
        response.setIsAvailable(entity.getSellers() != null && !entity.getSellers().isEmpty());

        return response;
    }

    public static List<ComputerResponse> toDTOList(List<ComputerEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(ComputerMapper::toDTO)
                .collect(Collectors.toList());
    }
}