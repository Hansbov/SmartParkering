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
import smartparking.domain.OpenHours;
import smartparking.repository.OpenHoursRepository;
import smartparking.repository.search.OpenHoursSearchRepository;
import smartparking.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link smartparking.domain.OpenHours}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class OpenHoursResource {

    private final Logger log = LoggerFactory.getLogger(OpenHoursResource.class);

    private static final String ENTITY_NAME = "openHours";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OpenHoursRepository openHoursRepository;

    private final OpenHoursSearchRepository openHoursSearchRepository;

    public OpenHoursResource(OpenHoursRepository openHoursRepository, OpenHoursSearchRepository openHoursSearchRepository) {
        this.openHoursRepository = openHoursRepository;
        this.openHoursSearchRepository = openHoursSearchRepository;
    }

    /**
     * {@code POST  /open-hours} : Create a new openHours.
     *
     * @param openHours the openHours to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new openHours, or with status {@code 400 (Bad Request)} if the openHours has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/open-hours")
    public ResponseEntity<OpenHours> createOpenHours(@RequestBody OpenHours openHours) throws URISyntaxException {
        log.debug("REST request to save OpenHours : {}", openHours);
        if (openHours.getId() != null) {
            throw new BadRequestAlertException("A new openHours cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OpenHours result = openHoursRepository.save(openHours);
        openHoursSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/open-hours/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /open-hours/:id} : Updates an existing openHours.
     *
     * @param id the id of the openHours to save.
     * @param openHours the openHours to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated openHours,
     * or with status {@code 400 (Bad Request)} if the openHours is not valid,
     * or with status {@code 500 (Internal Server Error)} if the openHours couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/open-hours/{id}")
    public ResponseEntity<OpenHours> updateOpenHours(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OpenHours openHours
    ) throws URISyntaxException {
        log.debug("REST request to update OpenHours : {}, {}", id, openHours);
        if (openHours.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, openHours.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!openHoursRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OpenHours result = openHoursRepository.save(openHours);
        openHoursSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, openHours.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /open-hours/:id} : Partial updates given fields of an existing openHours, field will ignore if it is null
     *
     * @param id the id of the openHours to save.
     * @param openHours the openHours to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated openHours,
     * or with status {@code 400 (Bad Request)} if the openHours is not valid,
     * or with status {@code 404 (Not Found)} if the openHours is not found,
     * or with status {@code 500 (Internal Server Error)} if the openHours couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/open-hours/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<OpenHours> partialUpdateOpenHours(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OpenHours openHours
    ) throws URISyntaxException {
        log.debug("REST request to partial update OpenHours partially : {}, {}", id, openHours);
        if (openHours.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, openHours.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!openHoursRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OpenHours> result = openHoursRepository
            .findById(openHours.getId())
            .map(
                existingOpenHours -> {
                    if (openHours.getWeekday() != null) {
                        existingOpenHours.setWeekday(openHours.getWeekday());
                    }
                    if (openHours.getOpeningHour() != null) {
                        existingOpenHours.setOpeningHour(openHours.getOpeningHour());
                    }
                    if (openHours.getClosingHour() != null) {
                        existingOpenHours.setClosingHour(openHours.getClosingHour());
                    }
                    if (openHours.getDate() != null) {
                        existingOpenHours.setDate(openHours.getDate());
                    }

                    return existingOpenHours;
                }
            )
            .map(openHoursRepository::save)
            .map(
                savedOpenHours -> {
                    openHoursSearchRepository.save(savedOpenHours);

                    return savedOpenHours;
                }
            );

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, openHours.getId().toString())
        );
    }

    /**
     * {@code GET  /open-hours} : get all the openHours.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of openHours in body.
     */
    @GetMapping("/open-hours")
    public List<OpenHours> getAllOpenHours() {
        log.debug("REST request to get all OpenHours");
        return openHoursRepository.findAll();
    }

    /**
     * {@code GET  /open-hours/:id} : get the "id" openHours.
     *
     * @param id the id of the openHours to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the openHours, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/open-hours/{id}")
    public ResponseEntity<OpenHours> getOpenHours(@PathVariable Long id) {
        log.debug("REST request to get OpenHours : {}", id);
        Optional<OpenHours> openHours = openHoursRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(openHours);
    }

    /**
     * {@code DELETE  /open-hours/:id} : delete the "id" openHours.
     *
     * @param id the id of the openHours to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/open-hours/{id}")
    public ResponseEntity<Void> deleteOpenHours(@PathVariable Long id) {
        log.debug("REST request to delete OpenHours : {}", id);
        openHoursRepository.deleteById(id);
        openHoursSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/open-hours?query=:query} : search for the openHours corresponding
     * to the query.
     *
     * @param query the query of the openHours search.
     * @return the result of the search.
     */
    @GetMapping("/_search/open-hours")
    public List<OpenHours> searchOpenHours(@RequestParam String query) {
        log.debug("REST request to search OpenHours for query {}", query);
        return StreamSupport
            .stream(openHoursSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
