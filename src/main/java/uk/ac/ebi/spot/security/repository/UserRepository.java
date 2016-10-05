package uk.ac.ebi.spot.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.security.model.OrcidUser;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Repository
public interface UserRepository extends JpaRepository<OrcidUser, String> {
    OrcidUser findByApikey(String apiKey);

    OrcidUser findByOrcid(String username);
}
