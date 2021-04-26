package smartparking.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartparking.domain.ParkingSpot;
import smartparking.repository.ParkingSpotRepository;
import smartparking.repository.search.ParkingSpotSearchRepository;
import smartparking.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link smartparking.domain.ParkingSpot}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ParkingSpotResource {

    private final Logger log = LoggerFactory.getLogger(ParkingSpotResource.class);

    private static final String ENTITY_NAME = "parkingSpot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingSpotSearchRepository parkingSpotSearchRepository;

    public ParkingSpotResource(ParkingSpotRepository parkingSpotRepository, ParkingSpotSearchRepository parkingSpotSearchRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingSpotSearchRepository = parkingSpotSearchRepository;
    }

    /**
     * {@code POST  /parking-spots} : Create a new parkingSpot.
     *
     * @param parkingSpot the parkingSpot to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parkingSpot, or with status {@code 400 (Bad Request)} if the parkingSpot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/parking-spots")
    public ResponseEntity<ParkingSpot> createParkingSpot(@Valid @RequestBody ParkingSpot parkingSpot) throws URISyntaxException {
        log.debug("REST request to save ParkingSpot : {}", parkingSpot);
        if (parkingSpot.getId() != null) {
            throw new BadRequestAlertException("A new parkingSpot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParkingSpot result = parkingSpotRepository.save(parkingSpot);
        parkingSpotSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/parking-spots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /parking-spots/:id} : Updates an existing parkingSpot.
     *
     * @param id the id of the parkingSpot to save.
     * @param parkingSpot the parkingSpot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkingSpot,
     * or with status {@code 400 (Bad Request)} if the parkingSpot is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parkingSpot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/parking-spots/{id}")
    public ResponseEntity<ParkingSpot> updateParkingSpot(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParkingSpot parkingSpot
    ) throws URISyntaxException {
        log.debug("REST request to update ParkingSpot : {}, {}", id, parkingSpot);
        if (parkingSpot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkingSpot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parkingSpotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ParkingSpot result = parkingSpotRepository.save(parkingSpot);
        parkingSpotSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parkingSpot.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /parking-spots/:id} : Partial updates given fields of an existing parkingSpot, field will ignore if it is null
     *
     * @param id the id of the parkingSpot to save.
     * @param parkingSpot the parkingSpot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parkingSpot,
     * or with status {@code 400 (Bad Request)} if the parkingSpot is not valid,
     * or with status {@code 404 (Not Found)} if the parkingSpot is not found,
     * or with status {@code 500 (Internal Server Error)} if the parkingSpot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/parking-spots/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ParkingSpot> partialUpdateParkingSpot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParkingSpot parkingSpot
    ) throws URISyntaxException {
        log.debug("REST request to partial update ParkingSpot partially : {}, {}", id, parkingSpot);
        if (parkingSpot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parkingSpot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parkingSpotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParkingSpot> result = parkingSpotRepository
            .findById(parkingSpot.getId())
            .map(
                existingParkingSpot -> {
                    if (parkingSpot.getAccessableParking() != null) {
                        existingParkingSpot.setAccessableParking(parkingSpot.getAccessableParking());
                    }
                    if (parkingSpot.getAvailable() != null) {
                        existingParkingSpot.setAvailable(parkingSpot.getAvailable());
                    }
                    if (parkingSpot.getFloor() != null) {
                        existingParkingSpot.setFloor(parkingSpot.getFloor());
                    }

                    return existingParkingSpot;
                }
            )
            .map(parkingSpotRepository::save)
            .map(
                savedParkingSpot -> {
                    parkingSpotSearchRepository.save(savedParkingSpot);

                    return savedParkingSpot;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parkingSpot.getId().toString())
        );
    }

    /**
     * {@code GET  /parking-spots} : get all the parkingSpots.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of parkingSpots in body.
     */
    @GetMapping("/parking-spots")
    public List<ParkingSpot> getAllParkingSpots() {
        log.debug("REST request to get all ParkingSpots");
        return parkingSpotRepository.findAll();
    }

    /**
     * {@code GET  /parking-spots/:id} : get the "id" parkingSpot.
     *
     * @param id the id of the parkingSpot to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parkingSpot, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/parking-spots/{id}")
    public ResponseEntity<ParkingSpot> getParkingSpot(@PathVariable Long id) {
        log.debug("REST request to get ParkingSpot : {}", id);
        Optional<ParkingSpot> parkingSpot = parkingSpotRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(parkingSpot);
    }

    /**
     * {@code DELETE  /parking-spots/:id} : delete the "id" parkingSpot.
     *
     * @param id the id of the parkingSpot to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/parking-spots/{id}")
    public ResponseEntity<Void> deleteParkingSpot(@PathVariable Long id) {
        log.debug("REST request to delete ParkingSpot : {}", id);
        parkingSpotRepository.deleteById(id);
        parkingSpotSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/parking-spots?query=:query} : search for the parkingSpot corresponding
     * to the query.
     *
     * @param query the query of the parkingSpot search.
     * @return the result of the search.
     */
    @GetMapping("/_search/parking-spots")
    public List<ParkingSpot> searchParkingSpots(@RequestParam String query) {
        log.debug("REST request to search ParkingSpots for query {}", query);
        return StreamSupport
            .stream(parkingSpotSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
