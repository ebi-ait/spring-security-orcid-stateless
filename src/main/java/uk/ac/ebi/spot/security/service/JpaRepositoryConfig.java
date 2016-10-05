package uk.ac.ebi.spot.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.ac.ebi.spot.security.model.OrcidUser;

import javax.persistence.EntityManagerFactory;


/**
 * @author Simon Jupp
 * @date 23/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "uk.ac.ebi.spot.security.repository", basePackageClasses = {OrcidUser.class}, transactionManagerRef = "users")
@EnableTransactionManagement
public class JpaRepositoryConfig {

    @Autowired
    javax.sql.DataSource dataSource;

    @Bean
    public EntityManagerFactory entityManagerFactory() {

      HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
      vendorAdapter.setGenerateDdl(true);

      LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
      factory.setJpaVendorAdapter(vendorAdapter);
      factory.setPackagesToScan("uk.ac.ebi.spot");
      factory.setDataSource(dataSource);
      factory.afterPropertiesSet();

      return factory.getObject();
    }

    @Bean(name = "users")
    public PlatformTransactionManager transactionManager() {

      JpaTransactionManager txManager = new JpaTransactionManager();
      txManager.setEntityManagerFactory(entityManagerFactory());
      return txManager;
    }
}