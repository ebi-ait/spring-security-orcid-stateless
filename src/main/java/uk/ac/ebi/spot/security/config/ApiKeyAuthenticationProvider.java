package uk.ac.ebi.spot.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.security.model.OrcidUser;
import uk.ac.ebi.spot.security.model.OrcidPrinciple;
import uk.ac.ebi.spot.security.model.Role;
import uk.ac.ebi.spot.security.service.OrcidUserService;

import java.util.List;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * The authentication provider that authenticates a user by api key. Authentication is done
 * by validating the api key against an existing user in the user repository
 *
 */
@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    OrcidUserService orcidUserService;

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {

        ApiKeyToken token = (ApiKeyToken)auth;

        try{
            //Authenticate token against redis or whatever you want

            OrcidUser orcidUser = orcidUserService.getUserByApiKey(token.getValue());
            List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

            if (orcidUser.getRole().equals(Role.ADMIN)) {
                authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER,ROLE_ADMIN");
            }


            OrcidPrinciple orcidPrinciple = new OrcidPrinciple(orcidUser.getGivenName(), orcidUser.getFamilyName(), orcidUser.getOrcid());

            //Our token resolved to a username so i went with this token...you could make your CustomToken take the principal.  getCredentials returns "NO_PASSWORD"..it gets cleared out anyways.  also the getAuthenticated for the thing you return should return true now
            return new UsernamePasswordAuthenticationToken(orcidPrinciple, auth.getCredentials(), authorityList);
        } catch(Exception e){
            //TODO throw appropriate AuthenticationException types
            throw new BadCredentialsException("Authentication failure", e);
        }


    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyToken.class.isAssignableFrom(authentication);
    }


}

