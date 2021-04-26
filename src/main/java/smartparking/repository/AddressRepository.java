package smartparking.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import smartparking.domain.Address;

/**
 * Spring Data SQL repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {}
