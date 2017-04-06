package uk.ac.ebi.spot.security.model;

/**
 * @author Simon Jupp
 * @since 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * An authenticated principle with a name and orcid id
 *
 */
public class OrcidPrinciple {
    private String givenName;
    private String familyName;
    private String orcid;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public OrcidPrinciple(String givenName, String familyName, String orcid) {

        this.givenName = givenName;
        this.familyName = familyName;
        this.orcid = orcid;
    }
}
