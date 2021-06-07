package dev.appkr.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import dev.appkr.IntegrationTest;
import dev.appkr.domain.Album;
import dev.appkr.repository.AlbumRepository;
import dev.appkr.service.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link AlbumResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class AlbumResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_PUBLISHED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/albums";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Album album;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createEntity(EntityManager em) {
        Album album = new Album().title(DEFAULT_TITLE).publishedAt(DEFAULT_PUBLISHED_AT);
        return album;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createUpdatedEntity(EntityManager em) {
        Album album = new Album().title(UPDATED_TITLE).publishedAt(UPDATED_PUBLISHED_AT);
        return album;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Album.class).block();
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
        album = createEntity(em);
    }

    @Test
    void createAlbum() throws Exception {
        int databaseSizeBeforeCreate = albumRepository.findAll().collectList().block().size();
        // Create the Album
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeCreate + 1);
        Album testAlbum = albumList.get(albumList.size() - 1);
        assertThat(testAlbum.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testAlbum.getPublishedAt()).isEqualTo(DEFAULT_PUBLISHED_AT);
    }

    @Test
    void createAlbumWithExistingId() throws Exception {
        // Create the Album with an existing ID
        album.setId(1L);

        int databaseSizeBeforeCreate = albumRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = albumRepository.findAll().collectList().block().size();
        // set the field null
        album.setTitle(null);

        // Create the Album, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPublishedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = albumRepository.findAll().collectList().block().size();
        // set the field null
        album.setPublishedAt(null);

        // Create the Album, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAlbums() {
        // Initialize the database
        albumRepository.save(album).block();

        // Get all the albumList
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
            .value(hasItem(album.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].publishedAt")
            .value(hasItem(DEFAULT_PUBLISHED_AT.toString()));
    }

    @Test
    void getAlbum() {
        // Initialize the database
        albumRepository.save(album).block();

        // Get the album
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, album.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(album.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.publishedAt")
            .value(is(DEFAULT_PUBLISHED_AT.toString()));
    }

    @Test
    void getNonExistingAlbum() {
        // Get the album
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAlbum() throws Exception {
        // Initialize the database
        albumRepository.save(album).block();

        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();

        // Update the album
        Album updatedAlbum = albumRepository.findById(album.getId()).block();
        updatedAlbum.title(UPDATED_TITLE).publishedAt(UPDATED_PUBLISHED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAlbum.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAlbum))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
        Album testAlbum = albumList.get(albumList.size() - 1);
        assertThat(testAlbum.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAlbum.getPublishedAt()).isEqualTo(UPDATED_PUBLISHED_AT);
    }

    @Test
    void putNonExistingAlbum() throws Exception {
        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();
        album.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, album.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAlbum() throws Exception {
        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();
        album.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAlbum() throws Exception {
        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();
        album.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAlbumWithPatch() throws Exception {
        // Initialize the database
        albumRepository.save(album).block();

        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();

        // Update the album using partial update
        Album partialUpdatedAlbum = new Album();
        partialUpdatedAlbum.setId(album.getId());

        partialUpdatedAlbum.publishedAt(UPDATED_PUBLISHED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAlbum.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAlbum))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
        Album testAlbum = albumList.get(albumList.size() - 1);
        assertThat(testAlbum.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testAlbum.getPublishedAt()).isEqualTo(UPDATED_PUBLISHED_AT);
    }

    @Test
    void fullUpdateAlbumWithPatch() throws Exception {
        // Initialize the database
        albumRepository.save(album).block();

        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();

        // Update the album using partial update
        Album partialUpdatedAlbum = new Album();
        partialUpdatedAlbum.setId(album.getId());

        partialUpdatedAlbum.title(UPDATED_TITLE).publishedAt(UPDATED_PUBLISHED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAlbum.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAlbum))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
        Album testAlbum = albumList.get(albumList.size() - 1);
        assertThat(testAlbum.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAlbum.getPublishedAt()).isEqualTo(UPDATED_PUBLISHED_AT);
    }

    @Test
    void patchNonExistingAlbum() throws Exception {
        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();
        album.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, album.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAlbum() throws Exception {
        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();
        album.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAlbum() throws Exception {
        int databaseSizeBeforeUpdate = albumRepository.findAll().collectList().block().size();
        album.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(album))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Album in the database
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAlbum() {
        // Initialize the database
        albumRepository.save(album).block();

        int databaseSizeBeforeDelete = albumRepository.findAll().collectList().block().size();

        // Delete the album
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, album.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Album> albumList = albumRepository.findAll().collectList().block();
        assertThat(albumList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
