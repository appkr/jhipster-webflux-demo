package dev.appkr.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import dev.appkr.IntegrationTest;
import dev.appkr.domain.Song;
import dev.appkr.repository.SongRepository;
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
 * Integration tests for the {@link SongResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class SongResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_PLAY_TIME = "AAAAAAAAAA";
    private static final String UPDATED_PLAY_TIME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/songs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Song song;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createEntity(EntityManager em) {
        Song song = new Song().title(DEFAULT_TITLE).playTime(DEFAULT_PLAY_TIME);
        return song;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Song createUpdatedEntity(EntityManager em) {
        Song song = new Song().title(UPDATED_TITLE).playTime(UPDATED_PLAY_TIME);
        return song;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Song.class).block();
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
        song = createEntity(em);
    }

    @Test
    void createSong() throws Exception {
        int databaseSizeBeforeCreate = songRepository.findAll().collectList().block().size();
        // Create the Song
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeCreate + 1);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSong.getPlayTime()).isEqualTo(DEFAULT_PLAY_TIME);
    }

    @Test
    void createSongWithExistingId() throws Exception {
        // Create the Song with an existing ID
        song.setId(1L);

        int databaseSizeBeforeCreate = songRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = songRepository.findAll().collectList().block().size();
        // set the field null
        song.setTitle(null);

        // Create the Song, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPlayTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = songRepository.findAll().collectList().block().size();
        // set the field null
        song.setPlayTime(null);

        // Create the Song, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSongs() {
        // Initialize the database
        songRepository.save(song).block();

        // Get all the songList
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
            .value(hasItem(song.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].playTime")
            .value(hasItem(DEFAULT_PLAY_TIME));
    }

    @Test
    void getSong() {
        // Initialize the database
        songRepository.save(song).block();

        // Get the song
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, song.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(song.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.playTime")
            .value(is(DEFAULT_PLAY_TIME));
    }

    @Test
    void getNonExistingSong() {
        // Get the song
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSong() throws Exception {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();

        // Update the song
        Song updatedSong = songRepository.findById(song.getId()).block();
        updatedSong.title(UPDATED_TITLE).playTime(UPDATED_PLAY_TIME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSong.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSong))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSong.getPlayTime()).isEqualTo(UPDATED_PLAY_TIME);
    }

    @Test
    void putNonExistingSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, song.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSongWithPatch() throws Exception {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();

        // Update the song using partial update
        Song partialUpdatedSong = new Song();
        partialUpdatedSong.setId(song.getId());

        partialUpdatedSong.title(UPDATED_TITLE).playTime(UPDATED_PLAY_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSong.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSong))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSong.getPlayTime()).isEqualTo(UPDATED_PLAY_TIME);
    }

    @Test
    void fullUpdateSongWithPatch() throws Exception {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();

        // Update the song using partial update
        Song partialUpdatedSong = new Song();
        partialUpdatedSong.setId(song.getId());

        partialUpdatedSong.title(UPDATED_TITLE).playTime(UPDATED_PLAY_TIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSong.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSong))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
        Song testSong = songList.get(songList.size() - 1);
        assertThat(testSong.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSong.getPlayTime()).isEqualTo(UPDATED_PLAY_TIME);
    }

    @Test
    void patchNonExistingSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, song.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSong() throws Exception {
        int databaseSizeBeforeUpdate = songRepository.findAll().collectList().block().size();
        song.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(song))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Song in the database
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSong() {
        // Initialize the database
        songRepository.save(song).block();

        int databaseSizeBeforeDelete = songRepository.findAll().collectList().block().size();

        // Delete the song
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, song.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Song> songList = songRepository.findAll().collectList().block();
        assertThat(songList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
