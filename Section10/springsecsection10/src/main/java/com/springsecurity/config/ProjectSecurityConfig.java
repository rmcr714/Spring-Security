package com.springsecurity.config;


import static org.springframework.security.config.Customizer.withDefaults;

import com.springsecurity.exceptionhandling.CustomAccessDeniedHandler;
import com.springsecurity.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import com.springsecurity.filter.AuthoritiesLoggingAfterFilter;
import com.springsecurity.filter.AuthoritiesLoggingAtFilter;
import com.springsecurity.filter.CsrfCookieFilter;
import com.springsecurity.filter.RequestValidationBeforeFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Profile("!prod")
public class ProjectSecurityConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    //This object is used to validate the csrf token we got from the ui is legit or not
    CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

    http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))  //This tells spring security to not store jession or token data in securityContext instead u take care of ti
        .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(
            SessionCreationPolicy.ALWAYS))   //This is done so that spring creates token or jsessionid token for each request, without this for ui app calls spring didn't create the jsession id
        .cors(corsConfig ->
            corsConfig.configurationSource(new CorsConfigurationSource() {   //Required for cors to allow any request coming from http://localhost:4200, u can put a different host on requirement
              @Override
              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                return config;
              }
            }))
        .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler) //This csrfTokeRequ...Handler object is used to check the csrf token passed as part of the request is valid or not
            .ignoringRequestMatchers("/contact","/register") // Won't need csrf for these two
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // This is set to false so that the client can read the csrf cookie, else they cant . This should be set to false
        .addFilterAt(new CsrfCookieFilter(), BasicAuthenticationFilter.class) // This sends the csrf token after the basic authentication is complete in a cookie for a request
        .addFilterBefore(new RequestValidationBeforeFilter(),BasicAuthenticationFilter.class) //This will execute our custom email check filter just before BasicAuthentication filter
        .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class) // Some custom logging filter to show how after filter works, here this will run after basic(account + password) filer
        .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
        .requiresChannel(rcc->rcc.anyRequest().requiresInsecure())  //Only http traffic will be allowed, good for local envs
        .authorizeHttpRequests((requests) -> requests    // These roles are db values in an authority table, and we will use them for authorizing users
            .requestMatchers("/myAccount").hasRole("USER")
            .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/myLoans").hasRole("USER")
            .requestMatchers("/myCards").hasRole("USER")
            .requestMatchers("/user").authenticated()
            .requestMatchers("/notices", "/contact", "/error","/register").permitAll());
    http.formLogin(withDefaults());
    //Since we are throwing custom exception, we need to have this lambda below
    http.httpBasic(hbc->hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));

    //Validation exception should be done at global level
    http.exceptionHandling(eh->eh.accessDeniedHandler(new CustomAccessDeniedHandler()));
    return http.build();
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /**
   * From Spring Security 6.3 version
   * @return
   */
  @Bean
  public CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }


}
