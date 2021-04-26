package smartparking.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import smartparking.domain.CarPark;

/**
 * Spring Data Elasticsearch repository for the {@link CarPark} entity.
 */
public interface CarParkSearchRepository extends ElasticsearchRepository<CarPark, Long> {}
