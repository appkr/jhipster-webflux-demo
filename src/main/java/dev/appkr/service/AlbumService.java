package dev.appkr.service;

import dev.appkr.domain.Album;
import dev.appkr.repository.AlbumRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Album}.
 */
@Service
@Transactional
public class AlbumService {

    private final Logger log = LoggerFactory.getLogger(AlbumService.class);

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    /**
     * Save a album.
     *
     * @param album the entity to save.
     * @return the persisted entity.
     */
    public Mono<Album> save(Album album) {
        log.debug("Request to save Album : {}", album);
        return albumRepository.save(album);
    }

    /**
     * Partially update a album.
     *
     * @param album the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Album> partialUpdate(Album album) {
        log.debug("Request to partially update Album : {}", album);

        return albumRepository
            .findById(album.getId())
            .map(
                existingAlbum -> {
                    if (album.getTitle() != null) {
                        existingAlbum.setTitle(album.getTitle());
                    }
                    if (album.getPublishedAt() != null) {
                        existingAlbum.setPublishedAt(album.getPublishedAt());
                    }

                    return existingAlbum;
                }
            )
            .flatMap(albumRepository::save);
    }

    /**
     * Get all the albums.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Album> findAll(Pageable pageable) {
        log.debug("Request to get all Albums");
        return albumRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of albums available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return albumRepository.count();
    }

    /**
     * Get one album by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Album> findOne(Long id) {
        log.debug("Request to get Album : {}", id);
        return albumRepository.findById(id);
    }

    /**
     * Delete the album by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Album : {}", id);
        return albumRepository.deleteById(id);
    }
}
