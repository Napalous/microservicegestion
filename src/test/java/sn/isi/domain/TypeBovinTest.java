package sn.isi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.isi.web.rest.TestUtil;

class TypeBovinTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TypeBovin.class);
        TypeBovin typeBovin1 = new TypeBovin();
        typeBovin1.setId(1L);
        TypeBovin typeBovin2 = new TypeBovin();
        typeBovin2.setId(typeBovin1.getId());
        assertThat(typeBovin1).isEqualTo(typeBovin2);
        typeBovin2.setId(2L);
        assertThat(typeBovin1).isNotEqualTo(typeBovin2);
        typeBovin1.setId(null);
        assertThat(typeBovin1).isNotEqualTo(typeBovin2);
    }
}
