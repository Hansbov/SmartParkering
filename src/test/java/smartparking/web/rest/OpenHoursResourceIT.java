package smartparking.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import smartparking.IntegrationTest;
import smartparking.domain.OpenHours;
import smartparking.repository.OpenHoursRepository;
import smartparking.repository.search.OpenHoursSearchRepository;

/**
 * Integration tests for the {@link OpenHoursResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OpenHoursResourceIT {

    private static final String DEFAULT_WEEKDAY = "AAAAAAAAAA";
    private static final String UPDATED_WEEKDAY = "BBBBBBBBBB";

    private static final String DEFAULT_OPENING_HOUR = "AAAAAAAAAA";
    private static final String UPDATED_OPENING_HOUR = "BBBBBBBBBB";

    private static final String DEFAULT_CLOSING_HOUR = "AAAAAAAAAA";
    private static final String UPDATED_CLOSING_HOUR = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/open-hours";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/open-hours";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OpenHoursRepository openHoursRepository;

    /**
     * This repository is mocked in the smartparking.repository.search test package.
     *
     * @see smartparking.repository.search.OpenHoursSearchRepositoryMockConfiguration
     */
    @Autowired
    private OpenHoursSearchRepository mockOpenHoursSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOpenHoursMockMvc;

    private OpenHours openHours;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OpenHours createEntity(EntityManager em) {
        OpenHours openHours = new OpenHours()
            .weekday(DEFAULT_WEEKDAY)
            .openingHour(DEFAULT_OPENING_HOUR)
            .closingHour(DEFAULT_CLOSING_HOUR)
            .date(DEFAULT_DATE);
        return openHours;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OpenHours createUpdatedEntity(EntityManager em) {
        OpenHours openHours = new OpenHours()
            .weekday(UPDATED_WEEKDAY)
            .openingHour(UPDATED_OPENING_HOUR)
            .closingHour(UPDATED_CLOSING_HOUR)
            .date(UPDATED_DATE);
        return openHours;
    }

    @BeforeEach
    public void initTest() {
        openHours = createEntity(em);
    }

    @Test
    @Transactional
    void createOpenHours() throws Exception {
        int databaseSizeBeforeCreate = openHoursRepository.findAll().size();
        // Create the OpenHours
        restOpenHoursMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(openHours)))
            .andExpect(status().isCreated());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeCreate + 1);
        OpenHours testOpenHours = openHoursList.get(openHoursList.size() - 1);
        assertThat(testOpenHours.getWeekday()).isEqualTo(DEFAULT_WEEKDAY);
        assertThat(testOpenHours.getOpeningHour()).isEqualTo(DEFAULT_OPENING_HOUR);
        assertThat(testOpenHours.getClosingHour()).isEqualTo(DEFAULT_CLOSING_HOUR);
        assertThat(testOpenHours.getDate()).isEqualTo(DEFAULT_DATE);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(1)).save(testOpenHours);
    }

    @Test
    @Transactional
    void createOpenHoursWithExistingId() throws Exception {
        // Create the OpenHours with an existing ID
        openHours.setId(1L);

        int databaseSizeBeforeCreate = openHoursRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOpenHoursMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(openHours)))
            .andExpect(status().isBadRequest());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeCreate);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(0)).save(openHours);
    }

    @Test
    @Transactional
    void getAllOpenHours() throws Exception {
        // Initialize the database
        openHoursRepository.saveAndFlush(openHours);

        // Get all the openHoursList
        restOpenHoursMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(openHours.getId().intValue())))
            .andExpect(jsonPath("$.[*].weekday").value(hasItem(DEFAULT_WEEKDAY)))
            .andExpect(jsonPath("$.[*].openingHour").value(hasItem(DEFAULT_OPENING_HOUR)))
            .andExpect(jsonPath("$.[*].closingHour").value(hasItem(DEFAULT_CLOSING_HOUR)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getOpenHours() throws Exception {
        // Initialize the database
        openHoursRepository.saveAndFlush(openHours);

        // Get the openHours
        restOpenHoursMockMvc
            .perform(get(ENTITY_API_URL_ID, openHours.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(openHours.getId().intValue()))
            .andExpect(jsonPath("$.weekday").value(DEFAULT_WEEKDAY))
            .andExpect(jsonPath("$.openingHour").value(DEFAULT_OPENING_HOUR))
            .andExpect(jsonPath("$.closingHour").value(DEFAULT_CLOSING_HOUR))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOpenHours() throws Exception {
        // Get the openHours
        restOpenHoursMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOpenHours() throws Exception {
        // Initialize the database
        openHoursRepository.saveAndFlush(openHours);

        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();

        // Update the openHours
        OpenHours updatedOpenHours = openHoursRepository.findById(openHours.getId()).get();
        // Disconnect from session so that the updates on updatedOpenHours are not directly saved in db
        em.detach(updatedOpenHours);
        updatedOpenHours.weekday(UPDATED_WEEKDAY).openingHour(UPDATED_OPENING_HOUR).closingHour(UPDATED_CLOSING_HOUR).date(UPDATED_DATE);

        restOpenHoursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedOpenHours.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedOpenHours))
            )
            .andExpect(status().isOk());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);
        OpenHours testOpenHours = openHoursList.get(openHoursList.size() - 1);
        assertThat(testOpenHours.getWeekday()).isEqualTo(UPDATED_WEEKDAY);
        assertThat(testOpenHours.getOpeningHour()).isEqualTo(UPDATED_OPENING_HOUR);
        assertThat(testOpenHours.getClosingHour()).isEqualTo(UPDATED_CLOSING_HOUR);
        assertThat(testOpenHours.getDate()).isEqualTo(UPDATED_DATE);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository).save(testOpenHours);
    }

    @Test
    @Transactional
    void putNonExistingOpenHours() throws Exception {
        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();
        openHours.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOpenHoursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, openHours.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(openHours))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(0)).save(openHours);
    }

    @Test
    @Transactional
    void putWithIdMismatchOpenHours() throws Exception {
        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();
        openHours.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpenHoursMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(openHours))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(0)).save(openHours);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOpenHours() throws Exception {
        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();
        openHours.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpenHoursMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(openHours)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(0)).save(openHours);
    }

    @Test
    @Transactional
    void partialUpdateOpenHoursWithPatch() throws Exception {
        // Initialize the database
        openHoursRepository.saveAndFlush(openHours);

        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();

        // Update the openHours using partial update
        OpenHours partialUpdatedOpenHours = new OpenHours();
        partialUpdatedOpenHours.setId(openHours.getId());

        partialUpdatedOpenHours.closingHour(UPDATED_CLOSING_HOUR).date(UPDATED_DATE);

        restOpenHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOpenHours.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOpenHours))
            )
            .andExpect(status().isOk());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);
        OpenHours testOpenHours = openHoursList.get(openHoursList.size() - 1);
        assertThat(testOpenHours.getWeekday()).isEqualTo(DEFAULT_WEEKDAY);
        assertThat(testOpenHours.getOpeningHour()).isEqualTo(DEFAULT_OPENING_HOUR);
        assertThat(testOpenHours.getClosingHour()).isEqualTo(UPDATED_CLOSING_HOUR);
        assertThat(testOpenHours.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateOpenHoursWithPatch() throws Exception {
        // Initialize the database
        openHoursRepository.saveAndFlush(openHours);

        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();

        // Update the openHours using partial update
        OpenHours partialUpdatedOpenHours = new OpenHours();
        partialUpdatedOpenHours.setId(openHours.getId());

        partialUpdatedOpenHours
            .weekday(UPDATED_WEEKDAY)
            .openingHour(UPDATED_OPENING_HOUR)
            .closingHour(UPDATED_CLOSING_HOUR)
            .date(UPDATED_DATE);

        restOpenHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOpenHours.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOpenHours))
            )
            .andExpect(status().isOk());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);
        OpenHours testOpenHours = openHoursList.get(openHoursList.size() - 1);
        assertThat(testOpenHours.getWeekday()).isEqualTo(UPDATED_WEEKDAY);
        assertThat(testOpenHours.getOpeningHour()).isEqualTo(UPDATED_OPENING_HOUR);
        assertThat(testOpenHours.getClosingHour()).isEqualTo(UPDATED_CLOSING_HOUR);
        assertThat(testOpenHours.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingOpenHours() throws Exception {
        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();
        openHours.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOpenHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, openHours.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(openHours))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(0)).save(openHours);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOpenHours() throws Exception {
        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();
        openHours.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpenHoursMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(openHours))
            )
            .andExpect(status().isBadRequest());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(0)).save(openHours);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOpenHours() throws Exception {
        int databaseSizeBeforeUpdate = openHoursRepository.findAll().size();
        openHours.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOpenHoursMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(openHours))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OpenHours in the database
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(0)).save(openHours);
    }

    @Test
    @Transactional
    void deleteOpenHours() throws Exception {
        // Initialize the database
        openHoursRepository.saveAndFlush(openHours);

        int databaseSizeBeforeDelete = openHoursRepository.findAll().size();

        // Delete the openHours
        restOpenHoursMockMvc
            .perform(delete(ENTITY_API_URL_ID, openHours.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OpenHours> openHoursList = openHoursRepository.findAll();
        assertThat(openHoursList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OpenHours in Elasticsearch
        verify(mockOpenHoursSearchRepository, times(1)).deleteById(openHours.getId());
    }

    @Test
    @Transactional
    void searchOpenHours() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        openHoursRepository.saveAndFlush(openHours);
        when(mockOpenHoursSearchRepository.search(queryStringQuery("id:" + openHours.getId())))
            .thenReturn(Collections.singletonList(openHours));

        // Search the openHours
        restOpenHoursMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + openHours.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(openHours.getId().intValue())))
            .andExpect(jsonPath("$.[*].weekday").value(hasItem(DEFAULT_WEEKDAY)))
            .andExpect(jsonPath("$.[*].openingHour").value(hasItem(DEFAULT_OPENING_HOUR)))
            .andExpect(jsonPath("$.[*].closingHour").value(hasItem(DEFAULT_CLOSING_HOUR)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }
}
