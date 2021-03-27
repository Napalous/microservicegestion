package sn.isi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.web.rest.TestUtil;

class BovinTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bovin.class);
        Bovin bovin1 = new Bovin();
        bovin1.setId(1L);
        Bovin bovin2 = new Bovin();
        bovin2.setId(bovin1.getId());
        assertThat(bovin1).isEqualTo(bovin2);
        bovin2.setId(2L);
        assertThat(bovin1).isNotEqualTo(bovin2);
        bovin1.setId(null);
        assertThat(bovin1).isNotEqualTo(bovin2);
    }
}
