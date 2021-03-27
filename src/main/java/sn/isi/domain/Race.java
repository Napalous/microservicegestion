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
 * A Race.
 */
@Entity
@Table(name = "race")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Race implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @OneToMany(mappedBy = "race")
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

    public Race id(Long id) {
        this.id = id;
        return this;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Race libelle(String libelle) {
        this.libelle = libelle;
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Race image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Race imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Set<Bovin> getBovins() {
        return this.bovins;
    }

    public Race bovins(Set<Bovin> bovins) {
        this.setBovins(bovins);
        return this;
    }

    public Race addBovin(Bovin bovin) {
        this.bovins.add(bovin);
        bovin.setRace(this);
        return this;
    }

    public Race removeBovin(Bovin bovin) {
        this.bovins.remove(bovin);
        bovin.setRace(null);
        return this;
    }

    public void setBovins(Set<Bovin> bovins) {
        if (this.bovins != null) {
            this.bovins.forEach(i -> i.setRace(null));
        }
        if (bovins != null) {
            bovins.forEach(i -> i.setRace(this));
        }
        this.bovins = bovins;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Race)) {
            return false;
        }
        return id != null && id.equals(((Race) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Race{" +
            "id=" + getId() +
            ", libelle='" + getLibelle() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            "}";
    }
}
