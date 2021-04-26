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
import smartparking.domain.ParkingSpot;
import smartparking.repository.ParkingSpotRepository;
import smartparking.repository.search.ParkingSpotSearchRepository;

/**
 * Integration tests for the {@link ParkingSpotResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ParkingSpotResourceIT {

    private static final Boolean DEFAULT_ACCESSABLE_PARKING = false;
    private static final Boolean UPDATED_ACCESSABLE_PARKING = true;

    private static final Boolean DEFAULT_AVAILABLE = false;
    private static final Boolean UPDATED_AVAILABLE = true;

    private static final Long DEFAULT_FLOOR = 1L;
    private static final Long UPDATED_FLOOR = 2L;

    private static final String ENTITY_API_URL = "/api/parking-spots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/parking-spots";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    /**
     * This repository is mocked in the smartparking.repository.search test package.
     *
     * @see smartparking.repository.search.ParkingSpotSearchRepositoryMockConfiguration
     */
    @Autowired
    private ParkingSpotSearchRepository mockParkingSpotSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParkingSpotMockMvc;

    private ParkingSpot parkingSpot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingSpot createEntity(EntityManager em) {
        ParkingSpot parkingSpot = new ParkingSpot()
            .accessableParking(DEFAULT_ACCESSABLE_PARKING)
            .available(DEFAULT_AVAILABLE)
            .floor(DEFAULT_FLOOR);
        return parkingSpot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParkingSpot createUpdatedEntity(EntityManager em) {
        ParkingSpot parkingSpot = new ParkingSpot()
            .accessableParking(UPDATED_ACCESSABLE_PARKING)
            .available(UPDATED_AVAILABLE)
            .floor(UPDATED_FLOOR);
        return parkingSpot;
    }

    @BeforeEach
    public void initTest() {
        parkingSpot = createEntity(em);
    }

    @Test
    @Transactional
    void createParkingSpot() throws Exception {
        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().size();
        // Create the ParkingSpot
        restParkingSpotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingSpot)))
            .andExpect(status().isCreated());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate + 1);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getAccessableParking()).isEqualTo(DEFAULT_ACCESSABLE_PARKING);
        assertThat(testParkingSpot.getAvailable()).isEqualTo(DEFAULT_AVAILABLE);
        assertThat(testParkingSpot.getFloor()).isEqualTo(DEFAULT_FLOOR);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(1)).save(testParkingSpot);
    }

    @Test
    @Transactional
    void createParkingSpotWithExistingId() throws Exception {
        // Create the ParkingSpot with an existing ID
        parkingSpot.setId(1L);

        int databaseSizeBeforeCreate = parkingSpotRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParkingSpotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingSpot)))
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeCreate);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(0)).save(parkingSpot);
    }

    @Test
    @Transactional
    void checkAvailableIsRequired() throws Exception {
        int databaseSizeBeforeTest = parkingSpotRepository.findAll().size();
        // set the field null
        parkingSpot.setAvailable(null);

        // Create the ParkingSpot, which fails.

        restParkingSpotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingSpot)))
            .andExpect(status().isBadRequest());

        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParkingSpots() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get all the parkingSpotList
        restParkingSpotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingSpot.getId().intValue())))
            .andExpect(jsonPath("$.[*].accessableParking").value(hasItem(DEFAULT_ACCESSABLE_PARKING.booleanValue())))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].floor").value(hasItem(DEFAULT_FLOOR.intValue())));
    }

    @Test
    @Transactional
    void getParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        // Get the parkingSpot
        restParkingSpotMockMvc
            .perform(get(ENTITY_API_URL_ID, parkingSpot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parkingSpot.getId().intValue()))
            .andExpect(jsonPath("$.accessableParking").value(DEFAULT_ACCESSABLE_PARKING.booleanValue()))
            .andExpect(jsonPath("$.available").value(DEFAULT_AVAILABLE.booleanValue()))
            .andExpect(jsonPath("$.floor").value(DEFAULT_FLOOR.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingParkingSpot() throws Exception {
        // Get the parkingSpot
        restParkingSpotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Update the parkingSpot
        ParkingSpot updatedParkingSpot = parkingSpotRepository.findById(parkingSpot.getId()).get();
        // Disconnect from session so that the updates on updatedParkingSpot are not directly saved in db
        em.detach(updatedParkingSpot);
        updatedParkingSpot.accessableParking(UPDATED_ACCESSABLE_PARKING).available(UPDATED_AVAILABLE).floor(UPDATED_FLOOR);

        restParkingSpotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedParkingSpot.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedParkingSpot))
            )
            .andExpect(status().isOk());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getAccessableParking()).isEqualTo(UPDATED_ACCESSABLE_PARKING);
        assertThat(testParkingSpot.getAvailable()).isEqualTo(UPDATED_AVAILABLE);
        assertThat(testParkingSpot.getFloor()).isEqualTo(UPDATED_FLOOR);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository).save(testParkingSpot);
    }

    @Test
    @Transactional
    void putNonExistingParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parkingSpot.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpot))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(0)).save(parkingSpot);
    }

    @Test
    @Transactional
    void putWithIdMismatchParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpot))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(0)).save(parkingSpot);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(parkingSpot)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(0)).save(parkingSpot);
    }

    @Test
    @Transactional
    void partialUpdateParkingSpotWithPatch() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Update the parkingSpot using partial update
        ParkingSpot partialUpdatedParkingSpot = new ParkingSpot();
        partialUpdatedParkingSpot.setId(parkingSpot.getId());

        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParkingSpot.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingSpot))
            )
            .andExpect(status().isOk());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getAccessableParking()).isEqualTo(DEFAULT_ACCESSABLE_PARKING);
        assertThat(testParkingSpot.getAvailable()).isEqualTo(DEFAULT_AVAILABLE);
        assertThat(testParkingSpot.getFloor()).isEqualTo(DEFAULT_FLOOR);
    }

    @Test
    @Transactional
    void fullUpdateParkingSpotWithPatch() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();

        // Update the parkingSpot using partial update
        ParkingSpot partialUpdatedParkingSpot = new ParkingSpot();
        partialUpdatedParkingSpot.setId(parkingSpot.getId());

        partialUpdatedParkingSpot.accessableParking(UPDATED_ACCESSABLE_PARKING).available(UPDATED_AVAILABLE).floor(UPDATED_FLOOR);

        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParkingSpot.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParkingSpot))
            )
            .andExpect(status().isOk());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);
        ParkingSpot testParkingSpot = parkingSpotList.get(parkingSpotList.size() - 1);
        assertThat(testParkingSpot.getAccessableParking()).isEqualTo(UPDATED_ACCESSABLE_PARKING);
        assertThat(testParkingSpot.getAvailable()).isEqualTo(UPDATED_AVAILABLE);
        assertThat(testParkingSpot.getFloor()).isEqualTo(UPDATED_FLOOR);
    }

    @Test
    @Transactional
    void patchNonExistingParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parkingSpot.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpot))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(0)).save(parkingSpot);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(parkingSpot))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(0)).save(parkingSpot);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParkingSpot() throws Exception {
        int databaseSizeBeforeUpdate = parkingSpotRepository.findAll().size();
        parkingSpot.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParkingSpotMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(parkingSpot))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParkingSpot in the database
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(0)).save(parkingSpot);
    }

    @Test
    @Transactional
    void deleteParkingSpot() throws Exception {
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);

        int databaseSizeBeforeDelete = parkingSpotRepository.findAll().size();

        // Delete the parkingSpot
        restParkingSpotMockMvc
            .perform(delete(ENTITY_API_URL_ID, parkingSpot.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ParkingSpot> parkingSpotList = parkingSpotRepository.findAll();
        assertThat(parkingSpotList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ParkingSpot in Elasticsearch
        verify(mockParkingSpotSearchRepository, times(1)).deleteById(parkingSpot.getId());
    }

    @Test
    @Transactional
    void searchParkingSpot() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        parkingSpotRepository.saveAndFlush(parkingSpot);
        when(mockParkingSpotSearchRepository.search(queryStringQuery("id:" + parkingSpot.getId())))
            .thenReturn(Collections.singletonList(parkingSpot));

        // Search the parkingSpot
        restParkingSpotMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + parkingSpot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parkingSpot.getId().intValue())))
            .andExpect(jsonPath("$.[*].accessableParking").value(hasItem(DEFAULT_ACCESSABLE_PARKING.booleanValue())))
            .andExpect(jsonPath("$.[*].available").value(hasItem(DEFAULT_AVAILABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].floor").value(hasItem(DEFAULT_FLOOR.intValue())));
    }
}
