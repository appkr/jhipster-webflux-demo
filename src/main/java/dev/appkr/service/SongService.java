package dev.appkr.service;

import dev.appkr.domain.Song;
import dev.appkr.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Song}.
 */
@Service
@Transactional
public class SongService {

    private final Logger log = LoggerFactory.getLogger(SongService.class);

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    /**
     * Save a song.
     *
     * @param song the entity to save.
     * @return the persisted entity.
     */
    public Mono<Song> save(Song song) {
        log.debug("Request to save Song : {}", song);
        return songRepository.save(song);
    }

    /**
     * Partially update a song.
     *
     * @param song the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Song> partialUpdate(Song song) {
        log.debug("Request to partially update Song : {}", song);

        return songRepository
            .findById(song.getId())
            .map(
                existingSong -> {
                    if (song.getTitle() != null) {
                        existingSong.setTitle(song.getTitle());
                    }
                    if (song.getPlayTime() != null) {
                        existingSong.setPlayTime(song.getPlayTime());
                    }

                    return existingSong;
                }
            )
            .flatMap(songRepository::save);
    }

    /**
     * Get all the songs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Song> findAll(Pageable pageable) {
        log.debug("Request to get all Songs");
        return songRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of songs available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return songRepository.count();
    }

    /**
     * Get one song by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Song> findOne(Long id) {
        log.debug("Request to get Song : {}", id);
        return songRepository.findById(id);
    }

    /**
     * Delete the song by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Song : {}", id);
        return songRepository.deleteById(id);
    }
}
