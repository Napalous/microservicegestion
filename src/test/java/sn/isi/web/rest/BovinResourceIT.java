package sn.isi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import sn.isi.domain.Bovin;
import sn.isi.repository.BovinRepository;

/**
 * Integration tests for the {@link BovinResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BovinResourceIT {

    private static final String DEFAULT_NUMERO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO = "BBBBBBBBBB";

    private static final String DEFAULT_SEXE = "AAAAAAAAAA";
    private static final String UPDATED_SEXE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATENAISSANCE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATENAISSANCE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/bovins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BovinRepository bovinRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBovinMockMvc;

    private Bovin bovin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bovin createEntity(EntityManager em) {
        Bovin bovin = new Bovin().numero(DEFAULT_NUMERO).sexe(DEFAULT_SEXE).datenaissance(DEFAULT_DATENAISSANCE);
        return bovin;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bovin createUpdatedEntity(EntityManager em) {
        Bovin bovin = new Bovin().numero(UPDATED_NUMERO).sexe(UPDATED_SEXE).datenaissance(UPDATED_DATENAISSANCE);
        return bovin;
    }

    @BeforeEach
    public void initTest() {
        bovin = createEntity(em);
    }

    @Test
    @Transactional
    void createBovin() throws Exception {
        int databaseSizeBeforeCreate = bovinRepository.findAll().size();
        // Create the Bovin
        restBovinMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isCreated());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeCreate + 1);
        Bovin testBovin = bovinList.get(bovinList.size() - 1);
        assertThat(testBovin.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testBovin.getSexe()).isEqualTo(DEFAULT_SEXE);
        assertThat(testBovin.getDatenaissance()).isEqualTo(DEFAULT_DATENAISSANCE);
    }

    @Test
    @Transactional
    void createBovinWithExistingId() throws Exception {
        // Create the Bovin with an existing ID
        bovin.setId(1L);

        int databaseSizeBeforeCreate = bovinRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBovinMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        int databaseSizeBeforeTest = bovinRepository.findAll().size();
        // set the field null
        bovin.setNumero(null);

        // Create the Bovin, which fails.

        restBovinMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSexeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bovinRepository.findAll().size();
        // set the field null
        bovin.setSexe(null);

        // Create the Bovin, which fails.

        restBovinMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDatenaissanceIsRequired() throws Exception {
        int databaseSizeBeforeTest = bovinRepository.findAll().size();
        // set the field null
        bovin.setDatenaissance(null);

        // Create the Bovin, which fails.

        restBovinMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBovins() throws Exception {
        // Initialize the database
        bovinRepository.saveAndFlush(bovin);

        // Get all the bovinList
        restBovinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bovin.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE)))
            .andExpect(jsonPath("$.[*].datenaissance").value(hasItem(DEFAULT_DATENAISSANCE.toString())));
    }

    @Test
    @Transactional
    void getBovin() throws Exception {
        // Initialize the database
        bovinRepository.saveAndFlush(bovin);

        // Get the bovin
        restBovinMockMvc
            .perform(get(ENTITY_API_URL_ID, bovin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bovin.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.sexe").value(DEFAULT_SEXE))
            .andExpect(jsonPath("$.datenaissance").value(DEFAULT_DATENAISSANCE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBovin() throws Exception {
        // Get the bovin
        restBovinMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBovin() throws Exception {
        // Initialize the database
        bovinRepository.saveAndFlush(bovin);

        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();

        // Update the bovin
        Bovin updatedBovin = bovinRepository.findById(bovin.getId()).get();
        // Disconnect from session so that the updates on updatedBovin are not directly saved in db
        em.detach(updatedBovin);
        updatedBovin.numero(UPDATED_NUMERO).sexe(UPDATED_SEXE).datenaissance(UPDATED_DATENAISSANCE);

        restBovinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBovin.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBovin))
            )
            .andExpect(status().isOk());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
        Bovin testBovin = bovinList.get(bovinList.size() - 1);
        assertThat(testBovin.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testBovin.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testBovin.getDatenaissance()).isEqualTo(UPDATED_DATENAISSANCE);
    }

    @Test
    @Transactional
    void putNonExistingBovin() throws Exception {
        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();
        bovin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBovinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bovin.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBovin() throws Exception {
        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();
        bovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBovinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBovin() throws Exception {
        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();
        bovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBovinMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBovinWithPatch() throws Exception {
        // Initialize the database
        bovinRepository.saveAndFlush(bovin);

        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();

        // Update the bovin using partial update
        Bovin partialUpdatedBovin = new Bovin();
        partialUpdatedBovin.setId(bovin.getId());

        partialUpdatedBovin.sexe(UPDATED_SEXE).datenaissance(UPDATED_DATENAISSANCE);

        restBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBovin.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBovin))
            )
            .andExpect(status().isOk());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
        Bovin testBovin = bovinList.get(bovinList.size() - 1);
        assertThat(testBovin.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testBovin.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testBovin.getDatenaissance()).isEqualTo(UPDATED_DATENAISSANCE);
    }

    @Test
    @Transactional
    void fullUpdateBovinWithPatch() throws Exception {
        // Initialize the database
        bovinRepository.saveAndFlush(bovin);

        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();

        // Update the bovin using partial update
        Bovin partialUpdatedBovin = new Bovin();
        partialUpdatedBovin.setId(bovin.getId());

        partialUpdatedBovin.numero(UPDATED_NUMERO).sexe(UPDATED_SEXE).datenaissance(UPDATED_DATENAISSANCE);

        restBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBovin.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBovin))
            )
            .andExpect(status().isOk());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
        Bovin testBovin = bovinList.get(bovinList.size() - 1);
        assertThat(testBovin.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testBovin.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testBovin.getDatenaissance()).isEqualTo(UPDATED_DATENAISSANCE);
    }

    @Test
    @Transactional
    void patchNonExistingBovin() throws Exception {
        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();
        bovin.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bovin.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBovin() throws Exception {
        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();
        bovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBovinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBovin() throws Exception {
        int databaseSizeBeforeUpdate = bovinRepository.findAll().size();
        bovin.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBovinMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bovin))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bovin in the database
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBovin() throws Exception {
        // Initialize the database
        bovinRepository.saveAndFlush(bovin);

        int databaseSizeBeforeDelete = bovinRepository.findAll().size();

        // Delete the bovin
        restBovinMockMvc
            .perform(delete(ENTITY_API_URL_ID, bovin.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bovin> bovinList = bovinRepository.findAll();
        assertThat(bovinList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
