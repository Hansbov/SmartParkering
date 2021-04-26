package smartparking.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import smartparking.domain.OpenHours;

/**
 * Spring Data Elasticsearch repository for the {@link OpenHours} entity.
 */
public interface OpenHoursSearchRepository extends ElasticsearchRepository<OpenHours, Long> {}
