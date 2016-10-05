package uk.ac.ebi.spot.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.ac.ebi.spot.security.model.OrcidUser;
import uk.ac.ebi.spot.security.model.OrcidPrinciple;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Simon Jupp
 * @date 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * This intercepts all requests and adds an authenticated user to the ModelAndView. This means all your controllers
 * will have access to the authenticated users details in the model.
 *
 * The model attributes are
 * username :
 * orcidid :
 * apikey :
 * role :
 *
 */
@Component
public class ModelAndViewHandlerInterceptor extends HandlerInterceptorAdapter {

    public ModelAndViewHandlerInterceptor() {
    }

    @Autowired
    OrcidUserService orcidUserService;


    @Override
    public void postHandle(final HttpServletRequest request,
                           final HttpServletResponse response, final Object handler,
                           final ModelAndView modelAndView) throws Exception {

        // check for an API key

        if (modelAndView != null) {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                if ( SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof OrcidPrinciple) {
                    OrcidPrinciple orcidPrinciple = (OrcidPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                    OrcidUser user = orcidUserService.getOrCreateUser(orcidPrinciple, SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                    modelAndView.getModelMap().addAttribute("username", orcidPrinciple.getGivenName());
                    modelAndView.getModelMap().addAttribute("orcid", orcidPrinciple.getOrcid());
                    modelAndView.getModelMap().addAttribute("apikey", user.getApikey());
                    modelAndView.getModelMap().addAttribute("role", user.getRole());
                }
            }
        }


    }
}

