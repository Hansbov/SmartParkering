package smartparking.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import smartparking.domain.UserExtra;

/**
 * Spring Data SQL repository for the UserExtra entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserExtraRepository extends JpaRepository<UserExtra, Long> {}
