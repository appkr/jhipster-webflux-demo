package dev.appkr.repository.rowmapper;

import dev.appkr.domain.Singer;
import dev.appkr.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Singer}, with proper type conversions.
 */
@Service
public class SingerRowMapper implements BiFunction<Row, String, Singer> {

    private final ColumnConverter converter;

    public SingerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Singer} stored in the database.
     */
    @Override
    public Singer apply(Row row, String prefix) {
        Singer entity = new Singer();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        return entity;
    }
}
