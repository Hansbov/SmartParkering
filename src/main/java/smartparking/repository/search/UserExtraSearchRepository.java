package smartparking.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import smartparking.domain.UserExtra;

/**
 * Spring Data Elasticsearch repository for the {@link UserExtra} entity.
 */
public interface UserExtraSearchRepository extends ElasticsearchRepository<UserExtra, Long> {}
