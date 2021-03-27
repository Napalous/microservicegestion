package sn.isi.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.isi.domain.Bovin;

/**
 * Spring Data SQL repository for the Bovin entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BovinRepository extends JpaRepository<Bovin, Long> {}
