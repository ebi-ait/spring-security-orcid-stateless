package uk.ac.ebi.spot.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.ac.ebi.spot.security.service.ModelAndViewHandlerInterceptor;

/**
 * @author Simon Jupp
 * @since 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * Registers interceptor to web mvc config
 *
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public ModelAndViewHandlerInterceptor getHandlerInterceptor() {
        return new ModelAndViewHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getHandlerInterceptor());

    }
}
