package dev.appkr.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import dev.appkr.IntegrationTest;
import dev.appkr.domain.Singer;
import dev.appkr.repository.SingerRepository;
import dev.appkr.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link SingerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class SingerResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/singers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SingerRepository singerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Singer singer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Singer createEntity(EntityManager em) {
        Singer singer = new Singer().name(DEFAULT_NAME);
        return singer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Singer createUpdatedEntity(EntityManager em) {
        Singer singer = new Singer().name(UPDATED_NAME);
        return singer;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Singer.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        singer = createEntity(em);
    }

    @Test
    void createSinger() throws Exception {
        int databaseSizeBeforeCreate = singerRepository.findAll().collectList().block().size();
        // Create the Singer
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeCreate + 1);
        Singer testSinger = singerList.get(singerList.size() - 1);
        assertThat(testSinger.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createSingerWithExistingId() throws Exception {
        // Create the Singer with an existing ID
        singer.setId(1L);

        int databaseSizeBeforeCreate = singerRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = singerRepository.findAll().collectList().block().size();
        // set the field null
        singer.setName(null);

        // Create the Singer, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSingers() {
        // Initialize the database
        singerRepository.save(singer).block();

        // Get all the singerList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(singer.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getSinger() {
        // Initialize the database
        singerRepository.save(singer).block();

        // Get the singer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, singer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(singer.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingSinger() {
        // Get the singer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSinger() throws Exception {
        // Initialize the database
        singerRepository.save(singer).block();

        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();

        // Update the singer
        Singer updatedSinger = singerRepository.findById(singer.getId()).block();
        updatedSinger.name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSinger.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSinger))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
        Singer testSinger = singerList.get(singerList.size() - 1);
        assertThat(testSinger.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingSinger() throws Exception {
        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();
        singer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, singer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSinger() throws Exception {
        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();
        singer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSinger() throws Exception {
        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();
        singer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSingerWithPatch() throws Exception {
        // Initialize the database
        singerRepository.save(singer).block();

        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();

        // Update the singer using partial update
        Singer partialUpdatedSinger = new Singer();
        partialUpdatedSinger.setId(singer.getId());

        partialUpdatedSinger.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSinger.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSinger))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
        Singer testSinger = singerList.get(singerList.size() - 1);
        assertThat(testSinger.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateSingerWithPatch() throws Exception {
        // Initialize the database
        singerRepository.save(singer).block();

        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();

        // Update the singer using partial update
        Singer partialUpdatedSinger = new Singer();
        partialUpdatedSinger.setId(singer.getId());

        partialUpdatedSinger.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSinger.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSinger))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
        Singer testSinger = singerList.get(singerList.size() - 1);
        assertThat(testSinger.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingSinger() throws Exception {
        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();
        singer.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, singer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSinger() throws Exception {
        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();
        singer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSinger() throws Exception {
        int databaseSizeBeforeUpdate = singerRepository.findAll().collectList().block().size();
        singer.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(singer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Singer in the database
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSinger() {
        // Initialize the database
        singerRepository.save(singer).block();

        int databaseSizeBeforeDelete = singerRepository.findAll().collectList().block().size();

        // Delete the singer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, singer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Singer> singerList = singerRepository.findAll().collectList().block();
        assertThat(singerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
