package uk.ac.ebi.spot.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.stereotype.Component;

import javax.persistence.*;

/**
 * @author Simon Jupp
 * @since 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * Represents an Authorised user in the system
 *
 */
@Entity
@Table(name="user")
@Component
public class OrcidUser {

    /**
    *
    */
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name="user_id")
   private Long userID;

   @Column(name="givenName", nullable = false, unique = false)
   private String givenName;

   @Column(name="familyName", nullable = true, unique = false)
   private String familyName;

   @Column(name="apikey")
   @JsonIgnore
   private String apikey;

   @Column(name="orcid")
   private String orcid;

   @Column(name="role")
   private Role role;


   public OrcidUser() {

   }
   public OrcidUser(String givenName, String familyName, String apikey, String orcid, Role role) {
      this.givenName = givenName;
      this.apikey = apikey;
      this.familyName = familyName;
      this.role = role;
      this.orcid = orcid;
   }

   public static long getSerialVersionUID() {
      return serialVersionUID;
   }

   public Long getUserID() {
      return userID;
   }

   public void setUserID(Long userID) {
      this.userID = userID;
   }

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

   public String getApikey() {
      return apikey;
   }

   public void setApikey(String apikey) {
      this.apikey = apikey;
   }

   public String getOrcid() {
      return orcid;
   }

   public void setOrcid(String orcid) {
      this.orcid = orcid;
   }

   public Role getRole() {
      return role;
   }

   public void setRole(Role role) {
      this.role = role;
   }

   @Override
   public String toString() {
      return "User [userID=" + userID + ", givenName=" + givenName
              + ", apikey=" + apikey;
   }


}
