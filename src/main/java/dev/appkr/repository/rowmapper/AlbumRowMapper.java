package dev.appkr.repository.rowmapper;

import dev.appkr.domain.Album;
import dev.appkr.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Album}, with proper type conversions.
 */
@Service
public class AlbumRowMapper implements BiFunction<Row, String, Album> {

    private final ColumnConverter converter;

    public AlbumRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Album} stored in the database.
     */
    @Override
    public Album apply(Row row, String prefix) {
        Album entity = new Album();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setPublishedAt(converter.fromRow(row, prefix + "_published_at", Instant.class));
        entity.setSingerId(converter.fromRow(row, prefix + "_singer_id", Long.class));
        entity.setSongsId(converter.fromRow(row, prefix + "_songs_id", Long.class));
        return entity;
    }
}
