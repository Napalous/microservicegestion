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
import sn.isi.domain.TypeBovin;
import sn.isi.repository.TypeBovinRepository;
import sn.isi.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.isi.domain.TypeBovin}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TypeBovinResource {

    private final Logger log = LoggerFactory.getLogger(TypeBovinResource.class);

    private static final String ENTITY_NAME = "microservicegestionTypeBovin";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypeBovinRepository typeBovinRepository;

    public TypeBovinResource(TypeBovinRepository typeBovinRepository) {
        this.typeBovinRepository = typeBovinRepository;
    }

    /**
     * {@code POST  /type-bovins} : Create a new typeBovin.
     *
     * @param typeBovin the typeBovin to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeBovin, or with status {@code 400 (Bad Request)} if the typeBovin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/type-bovins")
    public ResponseEntity<TypeBovin> createTypeBovin(@Valid @RequestBody TypeBovin typeBovin) throws URISyntaxException {
        log.debug("REST request to save TypeBovin : {}", typeBovin);
        if (typeBovin.getId() != null) {
            throw new BadRequestAlertException("A new typeBovin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TypeBovin result = typeBovinRepository.save(typeBovin);
        return ResponseEntity
            .created(new URI("/api/type-bovins/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /type-bovins/:id} : Updates an existing typeBovin.
     *
     * @param id the id of the typeBovin to save.
     * @param typeBovin the typeBovin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeBovin,
     * or with status {@code 400 (Bad Request)} if the typeBovin is not valid,
     * or with status {@code 500 (Internal Server Error)} if the typeBovin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/type-bovins/{id}")
    public ResponseEntity<TypeBovin> updateTypeBovin(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TypeBovin typeBovin
    ) throws URISyntaxException {
        log.debug("REST request to update TypeBovin : {}, {}", id, typeBovin);
        if (typeBovin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeBovin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeBovinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TypeBovin result = typeBovinRepository.save(typeBovin);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeBovin.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /type-bovins/:id} : Partial updates given fields of an existing typeBovin, field will ignore if it is null
     *
     * @param id the id of the typeBovin to save.
     * @param typeBovin the typeBovin to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeBovin,
     * or with status {@code 400 (Bad Request)} if the typeBovin is not valid,
     * or with status {@code 404 (Not Found)} if the typeBovin is not found,
     * or with status {@code 500 (Internal Server Error)} if the typeBovin couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/type-bovins/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TypeBovin> partialUpdateTypeBovin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TypeBovin typeBovin
    ) throws URISyntaxException {
        log.debug("REST request to partial update TypeBovin partially : {}, {}", id, typeBovin);
        if (typeBovin.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeBovin.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeBovinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TypeBovin> result = typeBovinRepository
            .findById(typeBovin.getId())
            .map(
                existingTypeBovin -> {
                    if (typeBovin.getLibelle() != null) {
                        existingTypeBovin.setLibelle(typeBovin.getLibelle());
                    }

                    return existingTypeBovin;
                }
            )
            .map(typeBovinRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeBovin.getId().toString())
        );
    }

    /**
     * {@code GET  /type-bovins} : get all the typeBovins.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of typeBovins in body.
     */
    @GetMapping("/type-bovins")
    public ResponseEntity<List<TypeBovin>> getAllTypeBovins(Pageable pageable) {
        log.debug("REST request to get a page of TypeBovins");
        Page<TypeBovin> page = typeBovinRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /type-bovins/:id} : get the "id" typeBovin.
     *
     * @param id the id of the typeBovin to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeBovin, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/type-bovins/{id}")
    public ResponseEntity<TypeBovin> getTypeBovin(@PathVariable Long id) {
        log.debug("REST request to get TypeBovin : {}", id);
        Optional<TypeBovin> typeBovin = typeBovinRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(typeBovin);
    }

    /**
     * {@code DELETE  /type-bovins/:id} : delete the "id" typeBovin.
     *
     * @param id the id of the typeBovin to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/type-bovins/{id}")
    public ResponseEntity<Void> deleteTypeBovin(@PathVariable Long id) {
        log.debug("REST request to delete TypeBovin : {}", id);
        typeBovinRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
