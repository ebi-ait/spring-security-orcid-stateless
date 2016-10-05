package uk.ac.ebi.spot.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * A manager for getting authentication keys via an API KEY
 *
 */
public class ApiKeyAuthenticationManager implements AuthenticationManager {

    @Bean
   	public AuthenticationProvider createCustomAuthenticationProvider()  {
   	    return new ApiKeyAuthenticationProvider();
   	}

   	@Autowired
   	ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return apiKeyAuthenticationProvider.authenticate(authentication);
    }
}
