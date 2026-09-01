package az.computer.demo.Service;

import az.computer.demo.Entity.CartEntity;
import az.computer.demo.Entity.ComputerEntity;
import az.computer.demo.Entity.UserEntity;
import az.computer.demo.Mapper.CartMapper;
import az.computer.demo.Repo.CartRepository;
import az.computer.demo.Repo.ComputerRepo;
import az.computer.demo.Repo.UserRepo;
import az.computer.demo.Response.CartResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ComputerRepo computerRepo;
    private final UserRepo userRepo;

    public void addToCart(Long computerId, String email) {
        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));

        ComputerEntity computer = computerRepo.findById(computerId)
                .orElseThrow(() -> new RuntimeException("Məhsul tapılmadı"));

        Optional<CartEntity> existingItem = cartRepository.findByUserAndComputer_Id(user, computerId);

        if (existingItem.isPresent()) {
            CartEntity cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartRepository.save(cartItem);
        } else {
            CartEntity newItem = new CartEntity();
            newItem.setUser(user);
            newItem.setComputer(computer);
            newItem.setQuantity(1);
            cartRepository.save(newItem);
        }
    }

    public List<CartResponse> getUserCart(String email) {
        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı"));
        return CartMapper.toDTOList(cartRepository.findAllByUser(user));
    }

    @Transactional
    public void updateQuantity(Long computerId, Integer quantity, String email) {
        CartEntity cartItem = cartRepository.findByComputer_IdAndUser_Email(computerId, email)
                .orElseThrow(() -> new RuntimeException("Səbətdə məhsul tapılmadı"));
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
    }

    @Transactional
    public void removeItem(Long computerId, String email) {
        cartRepository.deleteByComputer_IdAndUser_Email(computerId, email);
    }
}