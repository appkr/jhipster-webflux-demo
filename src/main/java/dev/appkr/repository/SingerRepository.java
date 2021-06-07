package dev.appkr.repository;

import dev.appkr.domain.Singer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Singer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SingerRepository extends R2dbcRepository<Singer, Long>, SingerRepositoryInternal {
    Flux<Singer> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Singer> findAll();

    @Override
    Mono<Singer> findById(Long id);

    @Override
    <S extends Singer> Mono<S> save(S entity);
}

interface SingerRepositoryInternal {
    <S extends Singer> Mono<S> insert(S entity);
    <S extends Singer> Mono<S> save(S entity);
    Mono<Integer> update(Singer entity);

    Flux<Singer> findAll();
    Mono<Singer> findById(Long id);
    Flux<Singer> findAllBy(Pageable pageable);
    Flux<Singer> findAllBy(Pageable pageable, Criteria criteria);
}
