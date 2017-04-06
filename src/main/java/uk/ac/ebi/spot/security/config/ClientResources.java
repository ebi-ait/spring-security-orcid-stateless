package uk.ac.ebi.spot.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author Simon Jupp
 * @since 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Component
class ClientResources {
	private OAuth2ProtectedResourceDetails client = new AuthorizationCodeResourceDetails();
	private ResourceServerProperties resource = new ResourceServerProperties();

	@Value("${orcid.admin:}")
	private Collection<String> admins;

	public OAuth2ProtectedResourceDetails getClient() {
		return client;
	}

	public ResourceServerProperties getResource() {
		return resource;
	}

	public Collection<String> getAdmins() {
		return admins;
	}
}
