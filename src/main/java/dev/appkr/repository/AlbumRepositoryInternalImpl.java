package dev.appkr.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import dev.appkr.domain.Album;
import dev.appkr.repository.rowmapper.AlbumRowMapper;
import dev.appkr.repository.rowmapper.SingerRowMapper;
import dev.appkr.repository.rowmapper.SongRowMapper;
import dev.appkr.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Album entity.
 */
@SuppressWarnings("unused")
class AlbumRepositoryInternalImpl implements AlbumRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final SingerRowMapper singerMapper;
    private final SongRowMapper songMapper;
    private final AlbumRowMapper albumMapper;

    private static final Table entityTable = Table.aliased("album", EntityManager.ENTITY_ALIAS);
    private static final Table singerTable = Table.aliased("singer", "singer");
    private static final Table songsTable = Table.aliased("song", "songs");

    public AlbumRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        SingerRowMapper singerMapper,
        SongRowMapper songMapper,
        AlbumRowMapper albumMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.singerMapper = singerMapper;
        this.songMapper = songMapper;
        this.albumMapper = albumMapper;
    }

    @Override
    public Flux<Album> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Album> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Album> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = AlbumSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(SingerSqlHelper.getColumns(singerTable, "singer"));
        columns.addAll(SongSqlHelper.getColumns(songsTable, "songs"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(singerTable)
            .on(Column.create("singer_id", entityTable))
            .equals(Column.create("id", singerTable))
            .leftOuterJoin(songsTable)
            .on(Column.create("songs_id", entityTable))
            .equals(Column.create("id", songsTable));

        String select = entityManager.createSelect(selectFrom, Album.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Album> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Album> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Album process(Row row, RowMetadata metadata) {
        Album entity = albumMapper.apply(row, "e");
        entity.setSinger(singerMapper.apply(row, "singer"));
        entity.setSongs(songMapper.apply(row, "songs"));
        return entity;
    }

    @Override
    public <S extends Album> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Album> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Album with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Album entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class AlbumSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("published_at", table, columnPrefix + "_published_at"));

        columns.add(Column.aliased("singer_id", table, columnPrefix + "_singer_id"));
        columns.add(Column.aliased("songs_id", table, columnPrefix + "_songs_id"));
        return columns;
    }
}
