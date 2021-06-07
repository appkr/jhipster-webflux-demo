package dev.appkr.web.rest;

import dev.appkr.domain.Album;
import dev.appkr.repository.AlbumRepository;
import dev.appkr.service.AlbumService;
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
 * REST controller for managing {@link dev.appkr.domain.Album}.
 */
@RestController
@RequestMapping("/api")
public class AlbumResource {

    private final Logger log = LoggerFactory.getLogger(AlbumResource.class);

    private static final String ENTITY_NAME = "album";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AlbumService albumService;

    private final AlbumRepository albumRepository;

    public AlbumResource(AlbumService albumService, AlbumRepository albumRepository) {
        this.albumService = albumService;
        this.albumRepository = albumRepository;
    }

    /**
     * {@code POST  /albums} : Create a new album.
     *
     * @param album the album to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new album, or with status {@code 400 (Bad Request)} if the album has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/albums")
    public Mono<ResponseEntity<Album>> createAlbum(@Valid @RequestBody Album album) throws URISyntaxException {
        log.debug("REST request to save Album : {}", album);
        if (album.getId() != null) {
            throw new BadRequestAlertException("A new album cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return albumService
            .save(album)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/albums/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /albums/:id} : Updates an existing album.
     *
     * @param id the id of the album to save.
     * @param album the album to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated album,
     * or with status {@code 400 (Bad Request)} if the album is not valid,
     * or with status {@code 500 (Internal Server Error)} if the album couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/albums/{id}")
    public Mono<ResponseEntity<Album>> updateAlbum(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Album album
    ) throws URISyntaxException {
        log.debug("REST request to update Album : {}, {}", id, album);
        if (album.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, album.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return albumRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return albumService
                        .save(album)
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
     * {@code PATCH  /albums/:id} : Partial updates given fields of an existing album, field will ignore if it is null
     *
     * @param id the id of the album to save.
     * @param album the album to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated album,
     * or with status {@code 400 (Bad Request)} if the album is not valid,
     * or with status {@code 404 (Not Found)} if the album is not found,
     * or with status {@code 500 (Internal Server Error)} if the album couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/albums/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Album>> partialUpdateAlbum(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Album album
    ) throws URISyntaxException {
        log.debug("REST request to partial update Album partially : {}, {}", id, album);
        if (album.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, album.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return albumRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Album> result = albumService.partialUpdate(album);

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
     * {@code GET  /albums} : get all the albums.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of albums in body.
     */
    @GetMapping("/albums")
    public Mono<ResponseEntity<List<Album>>> getAllAlbums(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Albums");
        return albumService
            .countAll()
            .zipWith(albumService.findAll(pageable).collectList())
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
     * {@code GET  /albums/:id} : get the "id" album.
     *
     * @param id the id of the album to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the album, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/albums/{id}")
    public Mono<ResponseEntity<Album>> getAlbum(@PathVariable Long id) {
        log.debug("REST request to get Album : {}", id);
        Mono<Album> album = albumService.findOne(id);
        return ResponseUtil.wrapOrNotFound(album);
    }

    /**
     * {@code DELETE  /albums/:id} : delete the "id" album.
     *
     * @param id the id of the album to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/albums/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteAlbum(@PathVariable Long id) {
        log.debug("REST request to delete Album : {}", id);
        return albumService
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
