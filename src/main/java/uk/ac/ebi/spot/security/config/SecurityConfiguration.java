package uk.ac.ebi.spot.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;
import uk.ac.ebi.spot.security.filter.ApiKeyFilter;
import uk.ac.ebi.spot.security.service.OrcidServerTokenService;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */

@EnableOAuth2Client
@Controller
@EnableGlobalMethodSecurity(securedEnabled = true, order = 10)
@Order(6)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
    OAuth2ClientContext oauth2ClientContext;


	@Value("${orcid.login.endpoint:/login}")
	String loginEndpoint;

	@Value("${orcid.logout.redirect:/}")
	String logoutEndpoint;

	@Value("${orcid.apiroot.regex:^/api.*}")
	String apiRootRegex;


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**").authorizeRequests().antMatchers("/**").permitAll().anyRequest()
				.authenticated().and()
				.exceptionHandling()
				.defaultAuthenticationEntryPointFor(new RESTAuthenticationEntryPoint(), new RegexRequestMatcher(apiRootRegex, "GET"))
				.defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint(loginEndpoint), new RegexRequestMatcher("^/.*", null)).and()
				.logout().logoutSuccessUrl(logoutEndpoint).permitAll().and()
				.csrf().disable() // todo
//				.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
	}

	@RequestMapping({ "login"})
	public String login(HttpServletRequest request) {
		return "login";
	}


	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	@Bean
	@ConfigurationProperties("orcid")
	ClientResources orcid() {
		return new ClientResources();
	}

	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(apiKeyFilter());
		filters.add(ssoFilter(orcid(), "/user"));
		filter.setFilters(filters);
		return filter;
	}


	@Bean
	ApiKeyAuthenticationManager apiKeyAuthenticaionManager() {
		return new ApiKeyAuthenticationManager();
	}

	@Autowired
	ApiKeyAuthenticationManager apiKeyAuthenticationManager;

	private Filter apiKeyFilter () {

		ApiKeyFilter apiKeyFilter =  new ApiKeyFilter(new RegexRequestMatcher(apiRootRegex, null));
		try {
			apiKeyFilter.setAuthenticationManager(this.apiKeyAuthenticationManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiKeyFilter;

	}


	private Filter ssoFilter(ClientResources client, String path) {
		OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationFilter = new OAuth2ClientAuthenticationProcessingFilter(
				path);

		OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);

		OrcidServerTokenService tokenServices = new OrcidServerTokenService(client.getResource().getUserInfoUri(),
				client.getClient().getClientId(), client.getAdmins());
		tokenServices.setRestTemplate(oAuth2RestTemplate);
		oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
		return oAuth2ClientAuthenticationFilter;
	}


}



