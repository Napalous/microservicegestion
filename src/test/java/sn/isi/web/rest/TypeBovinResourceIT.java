package sn.isi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.isi.IntegrationTest;
import sn.isi.domain.TypeBovin;
import sn.isi.repository.TypeBovinRepository;

/**
 * Integration tests for the {@link TypeBovinResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeBovinResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-bovins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypeBovinRepository typeBovinRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypeBovinMockMvc;

    private TypeBovin typeBovin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeBovin createEntity(EntityManager em) {
        TypeBovin typeBovin = new TypeBovin().libelle(DEFAULT_LIBELLE);
        return typeBovin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeBovin createUpdatedEntity(EntityManager em) {
        TypeBovin typeBovin = new TypeBovin().libelle(UPDATED_LIBELLE);
        return typeBovin;
    }

    @BeforeEach
    public void initTest() {
        typeBovin = createEntity(em);
    }

    @Test
    @Transactional
    void createTypeBovin() throws Exception {
        int databaseSizeBeforeCreate = typeBovinRepository.findAll().size();
        // Create the TypeBovin
        restTypeBovinMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isCreated());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeCreate + 1);
        TypeBovin testTypeBovin = typeBovinList.get(typeBovinList.size() - 1);
        assertThat(testTypeBovin.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void createTypeBovinWithExistingId() throws Exception {
        // Create the TypeBovin with an existing ID
        typeBovin.setId(1L);

        int databaseSizeBeforeCreate = typeBovinRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeBovinMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeBovinRepository.findAll().size();
        // set the field null
        typeBovin.setLibelle(null);

        // Create the TypeBovin, which fails.

        restTypeBovinMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isBadRequest());

        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTypeBovins() throws Exception {
        // Initialize the database
        typeBovinRepository.saveAndFlush(typeBovin);

        // Get all the typeBovinList
        restTypeBovinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeBovin.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getTypeBovin() throws Exception {
        // Initialize the database
        typeBovinRepository.saveAndFlush(typeBovin);

        // Get the typeBovin
        restTypeBovinMockMvc
            .perform(get(ENTITY_API_URL_ID, typeBovin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typeBovin.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getNonExistingTypeBovin() throws Exception {
        // Get the typeBovin
        restTypeBovinMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTypeBovin() throws Exception {
        // Initialize the database
        typeBovinRepository.saveAndFlush(typeBovin);

        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();

        // Update the typeBovin
        TypeBovin updatedTypeBovin = typeBovinRepository.findById(typeBovin.getId()).get();
        // Disconnect from session so that the updates on updatedTypeBovin are not directly saved in db
        em.detach(updatedTypeBovin);
        updatedTypeBovin.libelle(UPDATED_LIBELLE);

        restTypeBovinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTypeBovin.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTypeBovin))
            )
            .andExpect(status().isOk());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
        TypeBovin testTypeBovin = typeBovinList.get(typeBovinList.size() - 1);
        assertThat(testTypeBovin.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void putNonExistingTypeBovin() throws Exception {
        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();
        typeBovin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeBovinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeBovin.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypeBovin() throws Exception {
        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();
        typeBovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeBovinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypeBovin() throws Exception {
        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();
        typeBovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeBovinMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypeBovinWithPatch() throws Exception {
        // Initialize the database
        typeBovinRepository.saveAndFlush(typeBovin);

        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();

        // Update the typeBovin using partial update
        TypeBovin partialUpdatedTypeBovin = new TypeBovin();
        partialUpdatedTypeBovin.setId(typeBovin.getId());

        restTypeBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeBovin.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeBovin))
            )
            .andExpect(status().isOk());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
        TypeBovin testTypeBovin = typeBovinList.get(typeBovinList.size() - 1);
        assertThat(testTypeBovin.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void fullUpdateTypeBovinWithPatch() throws Exception {
        // Initialize the database
        typeBovinRepository.saveAndFlush(typeBovin);

        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();

        // Update the typeBovin using partial update
        TypeBovin partialUpdatedTypeBovin = new TypeBovin();
        partialUpdatedTypeBovin.setId(typeBovin.getId());

        partialUpdatedTypeBovin.libelle(UPDATED_LIBELLE);

        restTypeBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeBovin.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeBovin))
            )
            .andExpect(status().isOk());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
        TypeBovin testTypeBovin = typeBovinList.get(typeBovinList.size() - 1);
        assertThat(testTypeBovin.getLibelle()).isEqualTo(UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void patchNonExistingTypeBovin() throws Exception {
        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();
        typeBovin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typeBovin.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypeBovin() throws Exception {
        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();
        typeBovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypeBovin() throws Exception {
        int databaseSizeBeforeUpdate = typeBovinRepository.findAll().size();
        typeBovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeBovinMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeBovin))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeBovin in the database
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypeBovin() throws Exception {
        // Initialize the database
        typeBovinRepository.saveAndFlush(typeBovin);

        int databaseSizeBeforeDelete = typeBovinRepository.findAll().size();

        // Delete the typeBovin
        restTypeBovinMockMvc
            .perform(delete(ENTITY_API_URL_ID, typeBovin.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TypeBovin> typeBovinList = typeBovinRepository.findAll();
        assertThat(typeBovinList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
