package uk.ac.ebi.spot.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.security.model.OrcidUser;
import uk.ac.ebi.spot.security.model.OrcidPrinciple;
import uk.ac.ebi.spot.security.model.Role;
import uk.ac.ebi.spot.security.repository.UserRepository;
import uk.ac.ebi.spot.security.util.HashUtils;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * A service for getting and creating users from a user
 *
 */
//@Service
@Component
@Transactional("users")
public class OrcidUserService {

    @Autowired
    UserRepository userRepository;

    public OrcidUser getOrCreateUser (OrcidPrinciple orcidPrinciple, Collection<? extends GrantedAuthority> authorities) {

        OrcidUser user = userRepository.findByOrcid(orcidPrinciple.getOrcid());

        if (user == null) {
            String apiKey = generateApiKey(orcidPrinciple.getOrcid(), orcidPrinciple.getGivenName());
            Role role = Role.USER;

            if (userHasAuthority("ROLE_ADMIN", authorities)) {
                role = Role.ADMIN;
            }
            OrcidUser user1 = new OrcidUser(orcidPrinciple.getGivenName(), orcidPrinciple.getFamilyName(), apiKey, orcidPrinciple.getOrcid(),role);
            user = userRepository.save(user1);
        }
        return user;
    }

    public static boolean userHasAuthority(String authority, Collection<? extends GrantedAuthority> authorities) {

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

    public OrcidUser getUserByApiKey (String apiKey) {
        return userRepository.findByApikey(apiKey);
    }

    private String generateApiKey(String ordic, String givenName) {
        return HashUtils.generateHashEncodedID(ordic,
                givenName,
                String.valueOf(Math.random()),
                new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(new Date()));
    }
}
