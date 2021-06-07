package dev.appkr.repository.rowmapper;

import dev.appkr.domain.Song;
import dev.appkr.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Song}, with proper type conversions.
 */
@Service
public class SongRowMapper implements BiFunction<Row, String, Song> {

    private final ColumnConverter converter;

    public SongRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Song} stored in the database.
     */
    @Override
    public Song apply(Row row, String prefix) {
        Song entity = new Song();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setPlayTime(converter.fromRow(row, prefix + "_play_time", String.class));
        return entity;
    }
}
