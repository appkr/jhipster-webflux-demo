package dev.appkr.repository;

import dev.appkr.domain.Album;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Album entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AlbumRepository extends R2dbcRepository<Album, Long>, AlbumRepositoryInternal {
    Flux<Album> findAllBy(Pageable pageable);

    @Query("SELECT * FROM album entity WHERE entity.singer_id = :id")
    Flux<Album> findBySinger(Long id);

    @Query("SELECT * FROM album entity WHERE entity.singer_id IS NULL")
    Flux<Album> findAllWhereSingerIsNull();

    @Query("SELECT * FROM album entity WHERE entity.songs_id = :id")
    Flux<Album> findBySongs(Long id);

    @Query("SELECT * FROM album entity WHERE entity.songs_id IS NULL")
    Flux<Album> findAllWhereSongsIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Album> findAll();

    @Override
    Mono<Album> findById(Long id);

    @Override
    <S extends Album> Mono<S> save(S entity);
}

interface AlbumRepositoryInternal {
    <S extends Album> Mono<S> insert(S entity);
    <S extends Album> Mono<S> save(S entity);
    Mono<Integer> update(Album entity);

    Flux<Album> findAll();
    Mono<Album> findById(Long id);
    Flux<Album> findAllBy(Pageable pageable);
    Flux<Album> findAllBy(Pageable pageable, Criteria criteria);
}
