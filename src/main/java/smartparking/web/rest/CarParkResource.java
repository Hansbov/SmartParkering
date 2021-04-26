package smartparking.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import smartparking.domain.CarPark;
import smartparking.repository.CarParkRepository;
import smartparking.repository.search.CarParkSearchRepository;
import smartparking.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link smartparking.domain.CarPark}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CarParkResource {

    private final Logger log = LoggerFactory.getLogger(CarParkResource.class);

    private static final String ENTITY_NAME = "carPark";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarParkRepository carParkRepository;

    private final CarParkSearchRepository carParkSearchRepository;

    public CarParkResource(CarParkRepository carParkRepository, CarParkSearchRepository carParkSearchRepository) {
        this.carParkRepository = carParkRepository;
        this.carParkSearchRepository = carParkSearchRepository;
    }

    /**
     * {@code POST  /car-parks} : Create a new carPark.
     *
     * @param carPark the carPark to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carPark, or with status {@code 400 (Bad Request)} if the carPark has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/car-parks")
    public ResponseEntity<CarPark> createCarPark(@RequestBody CarPark carPark) throws URISyntaxException {
        log.debug("REST request to save CarPark : {}", carPark);
        if (carPark.getId() != null) {
            throw new BadRequestAlertException("A new carPark cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CarPark result = carParkRepository.save(carPark);
        carParkSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/car-parks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /car-parks/:id} : Updates an existing carPark.
     *
     * @param id the id of the carPark to save.
     * @param carPark the carPark to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carPark,
     * or with status {@code 400 (Bad Request)} if the carPark is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carPark couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/car-parks/{id}")
    public ResponseEntity<CarPark> updateCarPark(@PathVariable(value = "id", required = false) final Long id, @RequestBody CarPark carPark)
        throws URISyntaxException {
        log.debug("REST request to update CarPark : {}, {}", id, carPark);
        if (carPark.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carPark.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carParkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CarPark result = carParkRepository.save(carPark);
        carParkSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carPark.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /car-parks/:id} : Partial updates given fields of an existing carPark, field will ignore if it is null
     *
     * @param id the id of the carPark to save.
     * @param carPark the carPark to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carPark,
     * or with status {@code 400 (Bad Request)} if the carPark is not valid,
     * or with status {@code 404 (Not Found)} if the carPark is not found,
     * or with status {@code 500 (Internal Server Error)} if the carPark couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/car-parks/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CarPark> partialUpdateCarPark(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarPark carPark
    ) throws URISyntaxException {
        log.debug("REST request to partial update CarPark partially : {}, {}", id, carPark);
        if (carPark.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carPark.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carParkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarPark> result = carParkRepository
            .findById(carPark.getId())
            .map(
                existingCarPark -> {
                    if (carPark.getName() != null) {
                        existingCarPark.setName(carPark.getName());
                    }
                    if (carPark.getOwner() != null) {
                        existingCarPark.setOwner(carPark.getOwner());
                    }

                    return existingCarPark;
                }
            )
            .map(carParkRepository::save)
            .map(
                savedCarPark -> {
                    carParkSearchRepository.save(savedCarPark);

                    return savedCarPark;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carPark.getId().toString())
        );
    }

    /**
     * {@code GET  /car-parks} : get all the carParks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carParks in body.
     */
    @GetMapping("/car-parks")
    public List<CarPark> getAllCarParks() {
        log.debug("REST request to get all CarParks");
        return carParkRepository.findAll();
    }

    /**
     * {@code GET  /car-parks/:id} : get the "id" carPark.
     *
     * @param id the id of the carPark to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carPark, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/car-parks/{id}")
    public ResponseEntity<CarPark> getCarPark(@PathVariable Long id) {
        log.debug("REST request to get CarPark : {}", id);
        Optional<CarPark> carPark = carParkRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(carPark);
    }

    /**
     * {@code DELETE  /car-parks/:id} : delete the "id" carPark.
     *
     * @param id the id of the carPark to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/car-parks/{id}")
    public ResponseEntity<Void> deleteCarPark(@PathVariable Long id) {
        log.debug("REST request to delete CarPark : {}", id);
        carParkRepository.deleteById(id);
        carParkSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/car-parks?query=:query} : search for the carPark corresponding
     * to the query.
     *
     * @param query the query of the carPark search.
     * @return the result of the search.
     */
    @GetMapping("/_search/car-parks")
    public List<CarPark> searchCarParks(@RequestParam String query) {
        log.debug("REST request to search CarParks for query {}", query);
        return StreamSupport
            .stream(carParkSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
