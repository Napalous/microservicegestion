package sn.isi.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.isi.domain.Race;
import sn.isi.repository.RaceRepository;
import sn.isi.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.domain.Race}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RaceResource {

    private final Logger log = LoggerFactory.getLogger(RaceResource.class);

    private static final String ENTITY_NAME = "microservicegestionRace";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RaceRepository raceRepository;

    public RaceResource(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    /**
     * {@code POST  /races} : Create a new race.
     *
     * @param race the race to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new race, or with status {@code 400 (Bad Request)} if the race has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/races")
    public ResponseEntity<Race> createRace(@Valid @RequestBody Race race) throws URISyntaxException {
        log.debug("REST request to save Race : {}", race);
        if (race.getId() != null) {
            throw new BadRequestAlertException("A new race cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Race result = raceRepository.save(race);
        return ResponseEntity
            .created(new URI("/api/races/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /races/:id} : Updates an existing race.
     *
     * @param id the id of the race to save.
     * @param race the race to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated race,
     * or with status {@code 400 (Bad Request)} if the race is not valid,
     * or with status {@code 500 (Internal Server Error)} if the race couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/races/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Race race)
        throws URISyntaxException {
        log.debug("REST request to update Race : {}, {}", id, race);
        if (race.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, race.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!raceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Race result = raceRepository.save(race);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, race.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /races/:id} : Partial updates given fields of an existing race, field will ignore if it is null
     *
     * @param id the id of the race to save.
     * @param race the race to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated race,
     * or with status {@code 400 (Bad Request)} if the race is not valid,
     * or with status {@code 404 (Not Found)} if the race is not found,
     * or with status {@code 500 (Internal Server Error)} if the race couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/races/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Race> partialUpdateRace(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Race race
    ) throws URISyntaxException {
        log.debug("REST request to partial update Race partially : {}, {}", id, race);
        if (race.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, race.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!raceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Race> result = raceRepository
            .findById(race.getId())
            .map(
                existingRace -> {
                    if (race.getLibelle() != null) {
                        existingRace.setLibelle(race.getLibelle());
                    }
                    if (race.getImage() != null) {
                        existingRace.setImage(race.getImage());
                    }
                    if (race.getImageContentType() != null) {
                        existingRace.setImageContentType(race.getImageContentType());
                    }

                    return existingRace;
                }
            )
            .map(raceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, race.getId().toString())
        );
    }

    /**
     * {@code GET  /races} : get all the races.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of races in body.
     */
    @GetMapping("/races")
    public ResponseEntity<List<Race>> getAllRaces(Pageable pageable) {
        log.debug("REST request to get a page of Races");
        Page<Race> page = raceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /races/:id} : get the "id" race.
     *
     * @param id the id of the race to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the race, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/races/{id}")
    public ResponseEntity<Race> getRace(@PathVariable Long id) {
        log.debug("REST request to get Race : {}", id);
        Optional<Race> race = raceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(race);
    }

    /**
     * {@code DELETE  /races/:id} : delete the "id" race.
     *
     * @param id the id of the race to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/races/{id}")
    public ResponseEntity<Void> deleteRace(@PathVariable Long id) {
        log.debug("REST request to delete Race : {}", id);
        raceRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
