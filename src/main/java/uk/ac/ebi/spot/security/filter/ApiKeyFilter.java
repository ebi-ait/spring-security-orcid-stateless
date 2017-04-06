package uk.ac.ebi.spot.security.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import uk.ac.ebi.spot.security.config.ApiKeyToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Simon Jupp
 * @since 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * A http request filter to snoop for apikeys in the parameter. If a request requires authentication and an API key is provided
 * this class will set the current token with the API key.
 *
 */
public class ApiKeyFilter  extends AbstractAuthenticationProcessingFilter {


    public ApiKeyFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        //This filter only applies if the header is present
        if(StringUtils.isEmpty(((HttpServletRequest)req).getParameter("apikey"))) {
            chain.doFilter(req, res);
            return;
        }

        //On success keep going on the chain
        this.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                chain.doFilter(request, response);
            }
        });

        super.doFilter(req, res, chain);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {


        String tokenValue = request.getParameter("apikey");

        if(StringUtils.isEmpty(tokenValue)) {
            // should never get here
            return null;
        }


        ApiKeyToken token = new ApiKeyToken(AuthorityUtils.NO_AUTHORITIES, tokenValue);

        token.setDetails(authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(token);
    }


}
