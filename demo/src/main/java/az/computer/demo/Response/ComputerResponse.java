package az.computer.demo.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComputerResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;

    // Sənin o möhtəşəm Slider-in üçün lazım olan şəkil linkləri siyahısı
    private List<String> imageLinks;

    // Əlavə olaraq kompüterin statusunu (məsələn: satılıb/satılmayıb) görmək üçün
    private Boolean isAvailable;
}