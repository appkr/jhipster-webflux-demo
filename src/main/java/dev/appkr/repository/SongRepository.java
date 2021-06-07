package dev.appkr.repository;

import dev.appkr.domain.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Song entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SongRepository extends R2dbcRepository<Song, Long>, SongRepositoryInternal {
    Flux<Song> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Song> findAll();

    @Override
    Mono<Song> findById(Long id);

    @Override
    <S extends Song> Mono<S> save(S entity);
}

interface SongRepositoryInternal {
    <S extends Song> Mono<S> insert(S entity);
    <S extends Song> Mono<S> save(S entity);
    Mono<Integer> update(Song entity);

    Flux<Song> findAll();
    Mono<Song> findById(Long id);
    Flux<Song> findAllBy(Pageable pageable);
    Flux<Song> findAllBy(Pageable pageable, Criteria criteria);
}
