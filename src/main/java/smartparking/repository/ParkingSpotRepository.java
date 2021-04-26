package smartparking.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import smartparking.domain.ParkingSpot;

/**
 * Spring Data SQL repository for the ParkingSpot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {}
