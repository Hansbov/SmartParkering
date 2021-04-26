package smartparking.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import smartparking.domain.ParkingSpot;

/**
 * Spring Data Elasticsearch repository for the {@link ParkingSpot} entity.
 */
public interface ParkingSpotSearchRepository extends ElasticsearchRepository<ParkingSpot, Long> {}
