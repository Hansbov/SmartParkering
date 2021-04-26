package smartparking.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import smartparking.domain.Address;

/**
 * Spring Data Elasticsearch repository for the {@link Address} entity.
 */
public interface AddressSearchRepository extends ElasticsearchRepository<Address, Long> {}
