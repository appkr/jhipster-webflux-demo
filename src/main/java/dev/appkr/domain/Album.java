package dev.appkr.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Album.
 */
@Table("album")
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @NotNull(message = "must not be null")
    @Column("published_at")
    private Instant publishedAt;

    @Transient
    private Singer singer;

    @Column("singer_id")
    private Long singerId;

    @Transient
    private Song songs;

    @Column("songs_id")
    private Long songsId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Album id(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Album title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getPublishedAt() {
        return this.publishedAt;
    }

    public Album publishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Singer getSinger() {
        return this.singer;
    }

    public Album singer(Singer singer) {
        this.setSinger(singer);
        this.singerId = singer != null ? singer.getId() : null;
        return this;
    }

    public void setSinger(Singer singer) {
        this.singer = singer;
        this.singerId = singer != null ? singer.getId() : null;
    }

    public Long getSingerId() {
        return this.singerId;
    }

    public void setSingerId(Long singer) {
        this.singerId = singer;
    }

    public Song getSongs() {
        return this.songs;
    }

    public Album songs(Song song) {
        this.setSongs(song);
        this.songsId = song != null ? song.getId() : null;
        return this;
    }

    public void setSongs(Song song) {
        this.songs = song;
        this.songsId = song != null ? song.getId() : null;
    }

    public Long getSongsId() {
        return this.songsId;
    }

    public void setSongsId(Long song) {
        this.songsId = song;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Album)) {
            return false;
        }
        return id != null && id.equals(((Album) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Album{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            "}";
    }
}
