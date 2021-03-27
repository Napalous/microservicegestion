package sn.isi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Bovin.
 */
@Entity
@Table(name = "bovin")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Bovin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "numero", nullable = false)
    private String numero;

    @NotNull
    @Column(name = "sexe", nullable = false)
    private String sexe;

    @NotNull
    @Column(name = "datenaissance", nullable = false)
    private Instant datenaissance;

    @ManyToOne
    @JsonIgnoreProperties(value = { "bovins" }, allowSetters = true)
    private Race race;

    @ManyToOne
    @JsonIgnoreProperties(value = { "bovins" }, allowSetters = true)
    private TypeBovin typeBovin;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bovin id(Long id) {
        this.id = id;
        return this;
    }

    public String getNumero() {
        return this.numero;
    }

    public Bovin numero(String numero) {
        this.numero = numero;
        return this;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSexe() {
        return this.sexe;
    }

    public Bovin sexe(String sexe) {
        this.sexe = sexe;
        return this;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public Instant getDatenaissance() {
        return this.datenaissance;
    }

    public Bovin datenaissance(Instant datenaissance) {
        this.datenaissance = datenaissance;
        return this;
    }

    public void setDatenaissance(Instant datenaissance) {
        this.datenaissance = datenaissance;
    }

    public Race getRace() {
        return this.race;
    }

    public Bovin race(Race race) {
        this.setRace(race);
        return this;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public TypeBovin getTypeBovin() {
        return this.typeBovin;
    }

    public Bovin typeBovin(TypeBovin typeBovin) {
        this.setTypeBovin(typeBovin);
        return this;
    }

    public void setTypeBovin(TypeBovin typeBovin) {
        this.typeBovin = typeBovin;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bovin)) {
            return false;
        }
        return id != null && id.equals(((Bovin) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bovin{" +
            "id=" + getId() +
            ", numero='" + getNumero() + "'" +
            ", sexe='" + getSexe() + "'" +
            ", datenaissance='" + getDatenaissance() + "'" +
            "}";
    }
}
