package dev.appkr.service;

import dev.appkr.domain.Singer;
import dev.appkr.repository.SingerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Singer}.
 */
@Service
@Transactional
public class SingerService {

    private final Logger log = LoggerFactory.getLogger(SingerService.class);

    private final SingerRepository singerRepository;

    public SingerService(SingerRepository singerRepository) {
        this.singerRepository = singerRepository;
    }

    /**
     * Save a singer.
     *
     * @param singer the entity to save.
     * @return the persisted entity.
     */
    public Mono<Singer> save(Singer singer) {
        log.debug("Request to save Singer : {}", singer);
        return singerRepository.save(singer);
    }

    /**
     * Partially update a singer.
     *
     * @param singer the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Singer> partialUpdate(Singer singer) {
        log.debug("Request to partially update Singer : {}", singer);

        return singerRepository
            .findById(singer.getId())
            .map(
                existingSinger -> {
                    if (singer.getName() != null) {
                        existingSinger.setName(singer.getName());
                    }

                    return existingSinger;
                }
            )
            .flatMap(singerRepository::save);
    }

    /**
     * Get all the singers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Singer> findAll(Pageable pageable) {
        log.debug("Request to get all Singers");
        return singerRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of singers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return singerRepository.count();
    }

    /**
     * Get one singer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Singer> findOne(Long id) {
        log.debug("Request to get Singer : {}", id);
        return singerRepository.findById(id);
    }

    /**
     * Delete the singer by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Singer : {}", id);
        return singerRepository.deleteById(id);
    }
}
