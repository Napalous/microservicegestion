package sn.isi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.domain.TypeBovin;

/**
 * Spring Data SQL repository for the TypeBovin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeBovinRepository extends JpaRepository<TypeBovin, Long> {}
