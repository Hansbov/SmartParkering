package smartparking.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link OpenHoursSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class OpenHoursSearchRepositoryMockConfiguration {

    @MockBean
    private OpenHoursSearchRepository mockOpenHoursSearchRepository;
}
