package com.example.api.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class KeycloakSecurityConfiguration {

	// bug avec la montée de version de keycloak-spring-boot-starter à partir de la version 7.0.0
	// La découverte automatique de la configuration du Keycloak à partir du fichier  de properties ne fonctionne pas
	// Il faut déclarer un KeycloakSpringBootConfigResolver dans une classe de configuration à part
	// https://stackoverflow.com/questions/57787768/issues-running-example-keycloak-spring-boot-app
	@Configuration
	public static class KeycloakConfig {
		@Bean
		public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
			return new KeycloakSpringBootConfigResolver();
		}
	}
	
	// @KeycloakConfiguration englobe 3 annotations : @Configuration, @EnableWebSecurity et @ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
	@KeycloakConfiguration
	@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
	@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
	public static class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {
		
		@Bean
		@Override
		protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
			// dans le cadre d'une API, nous ne voulons pas de stratégie d'authentification de session (keycloak.bearer-only=true)
			return new NullAuthenticatedSessionStrategy();
		}
		
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
			// SimpleAuthorityMapper évite que les rôles soient préfixés par "ROLE_"
			keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
			// enregistrement de Keycloak comme fournisseur d'authentification auprès de Spring Security
			auth.authenticationProvider(keycloakAuthenticationProvider);
		}
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// disable csrf because of API mode
			http.csrf().disable().sessionManagement()
			// use previously declared bean
			.sessionAuthenticationStrategy(sessionAuthenticationStrategy()).sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			// keycloak filters for securisation
			.and().addFilterBefore(keycloakPreAuthActionsFilter(), LogoutFilter.class)
			.addFilterBefore(keycloakAuthenticationProcessingFilter(), X509AuthenticationFilter.class).exceptionHandling()
			.authenticationEntryPoint(authenticationEntryPoint()).and()
			// manage routes securisation here
			.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
			.antMatchers("/**").authenticated();
		}
	}
}
