package smartparking.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import smartparking.domain.OpenHours;

/**
 * Spring Data SQL repository for the OpenHours entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OpenHoursRepository extends JpaRepository<OpenHours, Long> {}
