package az.computer.demo.Repo;

import az.computer.demo.Entity.ComputerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComputerRepo extends JpaRepository<ComputerEntity,Long> {
}
