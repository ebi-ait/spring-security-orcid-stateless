package uk.ac.ebi.spot.security.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
public class ApiKeyToken extends AbstractAuthenticationToken {
    private final String value;

    public ApiKeyToken(Collection<? extends GrantedAuthority> authorities, String value) {
        super(authorities);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        //It doesn't make sense to let just anyone set this token to authenticated, so we block it
        //Similar precautions are taken in other spring framework tokens, EG: UsernamePasswordAuthenticationToken
        if (isAuthenticated) {

            throw new IllegalArgumentException("");
        }

        super.setAuthenticated(false);
    }
}