package uk.ac.ebi.spot.security.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.Assert;
import uk.ac.ebi.spot.security.config.OrcidPrincipleExtractor;
import uk.ac.ebi.spot.security.model.OrcidPrinciple;

import java.util.*;

/**
 * @author Simon Jupp
 * @since 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * This is adpated from UserTokenService - probably a better Spring way to override what I want to achieve here
 * I had to change from the github/facebook example as the Rest URL for a user requires the orcid id, which we first need to extract from
 * the authorization principle
 *
 */
public class OrcidServerTokenService  implements ResourceServerTokenServices {

	protected final Log logger = LogFactory.getLog(getClass());

	private final String userInfoEndpointUrl;

	private final String clientId;

	private OAuth2RestOperations restTemplate;

	private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;

	private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

	/**
	 * We force this class to extract an OrcidPrinciple using this extractor
	 */
	private OrcidPrincipleExtractor principalExtractor = new OrcidPrincipleExtractor();

	/**
	 *     First extension is to support passing a list of orcid ids
	 *     that will be used to create a user with admin authorities
	 *
 	 */
	private Collection<String> oricAdmins = new ArrayList<>();

	public OrcidServerTokenService(String userInfoEndpointUrl, String clientId, Collection<String> orcidAdmins) {
		this.userInfoEndpointUrl = userInfoEndpointUrl;
		this.clientId = clientId;
		this.oricAdmins = orcidAdmins;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public void setRestTemplate(OAuth2RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setAuthoritiesExtractor(AuthoritiesExtractor authoritiesExtractor) {
		Assert.notNull(authoritiesExtractor, "AuthoritiesExtractor must not be null");
		this.authoritiesExtractor = authoritiesExtractor;
	}

	public void setPrincipalExtractor(OrcidPrincipleExtractor principalExtractor) {
		Assert.notNull(principalExtractor, "PrincipalExtractor must not be null");
		this.principalExtractor = principalExtractor;
	}

	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		Map<String, Object> map = getMap(this.userInfoEndpointUrl, accessToken);
		if (map.containsKey("error")) {
			this.logger.debug("userinfo returned error: " + map.get("error"));
			throw new InvalidTokenException(accessToken);
		}
		return extractAuthentication(map);
	}

	/**
	 *
	 * This is where we inject any precofnigured roles into the token. In this case we check for admins
	 * and give them admin roles here
	 *
	 * @param map
	 * @return
     */
	private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
		Object principal = getPrincipal(map);
		List<GrantedAuthority> authorities = this.authoritiesExtractor
				.extractAuthorities(map);

		if (principal instanceof OrcidPrinciple) {
			OrcidPrinciple orcidPrinciple = (OrcidPrinciple) principal;
			if (oricAdmins.contains(orcidPrinciple.getOrcid())) {
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")) ;
			}
		}
		OAuth2Request request = new OAuth2Request(null, this.clientId, null, true, null,
				null, null, null, null);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				principal, "N/A", authorities);
		token.setDetails(map);
		return new OAuth2Authentication(request, token);
	}

	/**
	 * Return the principal that should be used for the token. The default implementation
	 * delegates to the {@link PrincipalExtractor}.
	 * @param map the source map
	 * @return the principal or {@literal "unknown"}
	 */
	protected Object getPrincipal(Map<String, Object> map) {
		Object principal = this.principalExtractor.extractPrincipal(map);
		return (principal == null ? "unknown" : principal);
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new UnsupportedOperationException("Not supported: read access token");
	}

	/**
	 * We have to modify the path to the Orcid Url to include the Orcid extracted from the principle token
	 * @param path
	 * @param accessToken
     * @return
     */
	@SuppressWarnings({ "unchecked" })
	private Map<String, Object> getMap(String path, String accessToken) {
		this.logger.info("Getting user info from: " + path);
		try {
			OAuth2RestOperations restTemplate = this.restTemplate;
			if (restTemplate == null) {
				BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
				resource.setClientId(this.clientId);
				restTemplate = new OAuth2RestTemplate(resource);
			}
			OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext()
					.getAccessToken();
			if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
				DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
						accessToken);
				token.setTokenType(this.tokenType);
				restTemplate.getOAuth2ClientContext().setAccessToken(token);
			}
			assert existingToken != null;
			path = path + existingToken.getAdditionalInformation().get("orcid");
			return restTemplate.getForEntity(path, Map.class).getBody();
		}
		catch (Exception ex) {
			this.logger.info("Could not fetch user details: " + ex.getClass() + ", "
					+ ex.getMessage());
			return Collections.<String, Object>singletonMap("error",
					"Could not fetch user details");
		}
	}

}
