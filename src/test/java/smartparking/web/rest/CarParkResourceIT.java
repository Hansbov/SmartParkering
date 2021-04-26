package smartparking.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import smartparking.domain.CarPark;
import smartparking.repository.CarParkRepository;
import smartparking.repository.search.CarParkSearchRepository;

/**
 * Integration tests for the {@link CarParkResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CarParkResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER = "AAAAAAAAAA";
    private static final String UPDATED_OWNER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/car-parks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/car-parks";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CarParkRepository carParkRepository;

    /**
     * This repository is mocked in the smartparking.repository.search test package.
     *
     * @see smartparking.repository.search.CarParkSearchRepositoryMockConfiguration
     */
    @Autowired
    private CarParkSearchRepository mockCarParkSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarParkMockMvc;

    private CarPark carPark;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarPark createEntity(EntityManager em) {
        CarPark carPark = new CarPark().name(DEFAULT_NAME).owner(DEFAULT_OWNER);
        return carPark;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarPark createUpdatedEntity(EntityManager em) {
        CarPark carPark = new CarPark().name(UPDATED_NAME).owner(UPDATED_OWNER);
        return carPark;
    }

    @BeforeEach
    public void initTest() {
        carPark = createEntity(em);
    }

    @Test
    @Transactional
    void createCarPark() throws Exception {
        int databaseSizeBeforeCreate = carParkRepository.findAll().size();
        // Create the CarPark
        restCarParkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carPark)))
            .andExpect(status().isCreated());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeCreate + 1);
        CarPark testCarPark = carParkList.get(carParkList.size() - 1);
        assertThat(testCarPark.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCarPark.getOwner()).isEqualTo(DEFAULT_OWNER);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(1)).save(testCarPark);
    }

    @Test
    @Transactional
    void createCarParkWithExistingId() throws Exception {
        // Create the CarPark with an existing ID
        carPark.setId(1L);

        int databaseSizeBeforeCreate = carParkRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarParkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carPark)))
            .andExpect(status().isBadRequest());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeCreate);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(0)).save(carPark);
    }

    @Test
    @Transactional
    void getAllCarParks() throws Exception {
        // Initialize the database
        carParkRepository.saveAndFlush(carPark);

        // Get all the carParkList
        restCarParkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carPark.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)));
    }

    @Test
    @Transactional
    void getCarPark() throws Exception {
        // Initialize the database
        carParkRepository.saveAndFlush(carPark);

        // Get the carPark
        restCarParkMockMvc
            .perform(get(ENTITY_API_URL_ID, carPark.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carPark.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER));
    }

    @Test
    @Transactional
    void getNonExistingCarPark() throws Exception {
        // Get the carPark
        restCarParkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCarPark() throws Exception {
        // Initialize the database
        carParkRepository.saveAndFlush(carPark);

        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();

        // Update the carPark
        CarPark updatedCarPark = carParkRepository.findById(carPark.getId()).get();
        // Disconnect from session so that the updates on updatedCarPark are not directly saved in db
        em.detach(updatedCarPark);
        updatedCarPark.name(UPDATED_NAME).owner(UPDATED_OWNER);

        restCarParkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCarPark.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCarPark))
            )
            .andExpect(status().isOk());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);
        CarPark testCarPark = carParkList.get(carParkList.size() - 1);
        assertThat(testCarPark.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarPark.getOwner()).isEqualTo(UPDATED_OWNER);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository).save(testCarPark);
    }

    @Test
    @Transactional
    void putNonExistingCarPark() throws Exception {
        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();
        carPark.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarParkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carPark.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carPark))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(0)).save(carPark);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarPark() throws Exception {
        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();
        carPark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(carPark))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(0)).save(carPark);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarPark() throws Exception {
        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();
        carPark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(carPark)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(0)).save(carPark);
    }

    @Test
    @Transactional
    void partialUpdateCarParkWithPatch() throws Exception {
        // Initialize the database
        carParkRepository.saveAndFlush(carPark);

        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();

        // Update the carPark using partial update
        CarPark partialUpdatedCarPark = new CarPark();
        partialUpdatedCarPark.setId(carPark.getId());

        partialUpdatedCarPark.name(UPDATED_NAME).owner(UPDATED_OWNER);

        restCarParkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarPark.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarPark))
            )
            .andExpect(status().isOk());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);
        CarPark testCarPark = carParkList.get(carParkList.size() - 1);
        assertThat(testCarPark.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarPark.getOwner()).isEqualTo(UPDATED_OWNER);
    }

    @Test
    @Transactional
    void fullUpdateCarParkWithPatch() throws Exception {
        // Initialize the database
        carParkRepository.saveAndFlush(carPark);

        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();

        // Update the carPark using partial update
        CarPark partialUpdatedCarPark = new CarPark();
        partialUpdatedCarPark.setId(carPark.getId());

        partialUpdatedCarPark.name(UPDATED_NAME).owner(UPDATED_OWNER);

        restCarParkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarPark.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCarPark))
            )
            .andExpect(status().isOk());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);
        CarPark testCarPark = carParkList.get(carParkList.size() - 1);
        assertThat(testCarPark.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCarPark.getOwner()).isEqualTo(UPDATED_OWNER);
    }

    @Test
    @Transactional
    void patchNonExistingCarPark() throws Exception {
        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();
        carPark.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarParkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carPark.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carPark))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(0)).save(carPark);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarPark() throws Exception {
        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();
        carPark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(carPark))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(0)).save(carPark);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarPark() throws Exception {
        int databaseSizeBeforeUpdate = carParkRepository.findAll().size();
        carPark.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(carPark)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarPark in the database
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(0)).save(carPark);
    }

    @Test
    @Transactional
    void deleteCarPark() throws Exception {
        // Initialize the database
        carParkRepository.saveAndFlush(carPark);

        int databaseSizeBeforeDelete = carParkRepository.findAll().size();

        // Delete the carPark
        restCarParkMockMvc
            .perform(delete(ENTITY_API_URL_ID, carPark.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CarPark> carParkList = carParkRepository.findAll();
        assertThat(carParkList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CarPark in Elasticsearch
        verify(mockCarParkSearchRepository, times(1)).deleteById(carPark.getId());
    }

    @Test
    @Transactional
    void searchCarPark() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        carParkRepository.saveAndFlush(carPark);
        when(mockCarParkSearchRepository.search(queryStringQuery("id:" + carPark.getId()))).thenReturn(Collections.singletonList(carPark));

        // Search the carPark
        restCarParkMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carPark.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carPark.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)));
    }
}
