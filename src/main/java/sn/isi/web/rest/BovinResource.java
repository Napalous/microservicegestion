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
import sn.isi.domain.Bovin;
import sn.isi.repository.BovinRepository;
import sn.isi.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.domain.Bovin}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BovinResource {

    private final Logger log = LoggerFactory.getLogger(BovinResource.class);

    private static final String ENTITY_NAME = "microservicegestionBovin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BovinRepository bovinRepository;

    public BovinResource(BovinRepository bovinRepository) {
        this.bovinRepository = bovinRepository;
    }

    /**
     * {@code POST  /bovins} : Create a new bovin.
     *
     * @param bovin the bovin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bovin, or with status {@code 400 (Bad Request)} if the bovin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bovins")
    public ResponseEntity<Bovin> createBovin(@Valid @RequestBody Bovin bovin) throws URISyntaxException {
        log.debug("REST request to save Bovin : {}", bovin);
        if (bovin.getId() != null) {
            throw new BadRequestAlertException("A new bovin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Bovin result = bovinRepository.save(bovin);
        return ResponseEntity
            .created(new URI("/api/bovins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bovins/:id} : Updates an existing bovin.
     *
     * @param id the id of the bovin to save.
     * @param bovin the bovin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bovin,
     * or with status {@code 400 (Bad Request)} if the bovin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bovin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bovins/{id}")
    public ResponseEntity<Bovin> updateBovin(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Bovin bovin)
        throws URISyntaxException {
        log.debug("REST request to update Bovin : {}, {}", id, bovin);
        if (bovin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bovin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bovinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Bovin result = bovinRepository.save(bovin);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bovin.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bovins/:id} : Partial updates given fields of an existing bovin, field will ignore if it is null
     *
     * @param id the id of the bovin to save.
     * @param bovin the bovin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bovin,
     * or with status {@code 400 (Bad Request)} if the bovin is not valid,
     * or with status {@code 404 (Not Found)} if the bovin is not found,
     * or with status {@code 500 (Internal Server Error)} if the bovin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bovins/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Bovin> partialUpdateBovin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Bovin bovin
    ) throws URISyntaxException {
        log.debug("REST request to partial update Bovin partially : {}, {}", id, bovin);
        if (bovin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bovin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bovinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bovin> result = bovinRepository
            .findById(bovin.getId())
            .map(
                existingBovin -> {
                    if (bovin.getNumero() != null) {
                        existingBovin.setNumero(bovin.getNumero());
                    }
                    if (bovin.getSexe() != null) {
                        existingBovin.setSexe(bovin.getSexe());
                    }
                    if (bovin.getDatenaissance() != null) {
                        existingBovin.setDatenaissance(bovin.getDatenaissance());
                    }

                    return existingBovin;
                }
            )
            .map(bovinRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bovin.getId().toString())
        );
    }

    /**
     * {@code GET  /bovins} : get all the bovins.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bovins in body.
     */
    @GetMapping("/bovins")
    public ResponseEntity<List<Bovin>> getAllBovins(Pageable pageable) {
        log.debug("REST request to get a page of Bovins");
        Page<Bovin> page = bovinRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bovins/:id} : get the "id" bovin.
     *
     * @param id the id of the bovin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bovin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bovins/{id}")
    public ResponseEntity<Bovin> getBovin(@PathVariable Long id) {
        log.debug("REST request to get Bovin : {}", id);
        Optional<Bovin> bovin = bovinRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(bovin);
    }

    /**
     * {@code DELETE  /bovins/:id} : delete the "id" bovin.
     *
     * @param id the id of the bovin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bovins/{id}")
    public ResponseEntity<Void> deleteBovin(@PathVariable Long id) {
        log.debug("REST request to delete Bovin : {}", id);
        bovinRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
