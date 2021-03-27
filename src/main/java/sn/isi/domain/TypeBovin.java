package sn.isi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TypeBovin.
 */
@Entity
@Table(name = "type_bovin")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TypeBovin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "libelle", nullable = false)
    private String libelle;

    @OneToMany(mappedBy = "typeBovin")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "race", "typeBovin" }, allowSetters = true)
    private Set<Bovin> bovins = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeBovin id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public TypeBovin libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Set<Bovin> getBovins() {
        return this.bovins;
    }

    public TypeBovin bovins(Set<Bovin> bovins) {
        this.setBovins(bovins);
        return this;
    }

    public TypeBovin addBovin(Bovin bovin) {
        this.bovins.add(bovin);
        bovin.setTypeBovin(this);
        return this;
    }

    public TypeBovin removeBovin(Bovin bovin) {
        this.bovins.remove(bovin);
        bovin.setTypeBovin(null);
        return this;
    }

    public void setBovins(Set<Bovin> bovins) {
        if (this.bovins != null) {
            this.bovins.forEach(i -> i.setTypeBovin(null));
        }
        if (bovins != null) {
            bovins.forEach(i -> i.setTypeBovin(this));
        }
        this.bovins = bovins;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeBovin)) {
            return false;
        }
        return id != null && id.equals(((TypeBovin) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypeBovin{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            "}";
    }
}
