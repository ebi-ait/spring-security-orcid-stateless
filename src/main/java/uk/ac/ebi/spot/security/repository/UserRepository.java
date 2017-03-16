package uk.ac.ebi.spot.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.security.model.OrcidUser;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Repository
@RestResource(exported=false)
public interface UserRepository extends JpaRepository<OrcidUser, String> {
    OrcidUser findByApikey(String apiKey);

    OrcidUser findByOrcid(String username);
}
