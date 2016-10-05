package uk.ac.ebi.spot.security.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import uk.ac.ebi.spot.security.model.OrcidPrinciple;

import java.util.Map;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * Creates an OrcidPrinciple from the object returned by the orcid authentication service
 *
 */
public class OrcidPrincipleExtractor implements PrincipalExtractor {
    @Override
    public OrcidPrinciple extractPrincipal(Map<String, Object> map) {

        Map orcidProfile = (Map) map.get("orcid-profile");
        String orcid = (String) ( (Map) orcidProfile.get("orcid-identifier")).get("path");
        Map details = (Map) ( (Map) orcidProfile.get("orcid-bio")).get("personal-details");

        String givenName = (String) ( (Map) details.get("given-names")).get("value");
        String familyName = "";

        if (( (Map) details.get("family-name")) != null) {
          familyName = (String) ( (Map) details.get("family-name")).get("value");
        }

        OrcidPrinciple user = new OrcidPrinciple(
                givenName,
                familyName,
                orcid
        );
        return user;
    }


}
