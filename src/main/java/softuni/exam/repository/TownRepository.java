package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.Town;

import java.util.Optional;

public interface TownRepository extends JpaRepository<Town, Integer> {
    Optional<Town> findByTownName(String townName);
}
