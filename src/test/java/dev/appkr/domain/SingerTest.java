package dev.appkr.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dev.appkr.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SingerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Singer.class);
        Singer singer1 = new Singer();
        singer1.setId(1L);
        Singer singer2 = new Singer();
        singer2.setId(singer1.getId());
        assertThat(singer1).isEqualTo(singer2);
        singer2.setId(2L);
        assertThat(singer1).isNotEqualTo(singer2);
        singer1.setId(null);
        assertThat(singer1).isNotEqualTo(singer2);
    }
}
