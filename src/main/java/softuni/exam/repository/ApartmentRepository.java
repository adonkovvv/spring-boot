package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.Town;

import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {
    Optional<Apartment> findByTownAndArea(Town town, Double area);
}
