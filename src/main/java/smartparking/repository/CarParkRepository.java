package smartparking.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import smartparking.domain.CarPark;

/**
 * Spring Data SQL repository for the CarPark entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarParkRepository extends JpaRepository<CarPark, Long> {}
