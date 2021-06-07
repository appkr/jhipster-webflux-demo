package dev.appkr.web.rest;

import dev.appkr.domain.Singer;
import dev.appkr.repository.SingerRepository;
import dev.appkr.service.SingerService;
import dev.appkr.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link dev.appkr.domain.Singer}.
 */
@RestController
@RequestMapping("/api")
public class SingerResource {

    private final Logger log = LoggerFactory.getLogger(SingerResource.class);

    private static final String ENTITY_NAME = "singer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SingerService singerService;

    private final SingerRepository singerRepository;

    public SingerResource(SingerService singerService, SingerRepository singerRepository) {
        this.singerService = singerService;
        this.singerRepository = singerRepository;
    }

    /**
     * {@code POST  /singers} : Create a new singer.
     *
     * @param singer the singer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new singer, or with status {@code 400 (Bad Request)} if the singer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/singers")
    public Mono<ResponseEntity<Singer>> createSinger(@Valid @RequestBody Singer singer) throws URISyntaxException {
        log.debug("REST request to save Singer : {}", singer);
        if (singer.getId() != null) {
            throw new BadRequestAlertException("A new singer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return singerService
            .save(singer)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/singers/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /singers/:id} : Updates an existing singer.
     *
     * @param id the id of the singer to save.
     * @param singer the singer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated singer,
     * or with status {@code 400 (Bad Request)} if the singer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the singer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/singers/{id}")
    public Mono<ResponseEntity<Singer>> updateSinger(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Singer singer
    ) throws URISyntaxException {
        log.debug("REST request to update Singer : {}, {}", id, singer);
        if (singer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, singer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return singerRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return singerService
                        .save(singer)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /singers/:id} : Partial updates given fields of an existing singer, field will ignore if it is null
     *
     * @param id the id of the singer to save.
     * @param singer the singer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated singer,
     * or with status {@code 400 (Bad Request)} if the singer is not valid,
     * or with status {@code 404 (Not Found)} if the singer is not found,
     * or with status {@code 500 (Internal Server Error)} if the singer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/singers/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Singer>> partialUpdateSinger(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Singer singer
    ) throws URISyntaxException {
        log.debug("REST request to partial update Singer partially : {}, {}", id, singer);
        if (singer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, singer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return singerRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Singer> result = singerService.partialUpdate(singer);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString())
                                    )
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /singers} : get all the singers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of singers in body.
     */
    @GetMapping("/singers")
    public Mono<ResponseEntity<List<Singer>>> getAllSingers(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Singers");
        return singerService
            .countAll()
            .zipWith(singerService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /singers/:id} : get the "id" singer.
     *
     * @param id the id of the singer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the singer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/singers/{id}")
    public Mono<ResponseEntity<Singer>> getSinger(@PathVariable Long id) {
        log.debug("REST request to get Singer : {}", id);
        Mono<Singer> singer = singerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(singer);
    }

    /**
     * {@code DELETE  /singers/:id} : delete the "id" singer.
     *
     * @param id the id of the singer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/singers/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSinger(@PathVariable Long id) {
        log.debug("REST request to delete Singer : {}", id);
        return singerService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
