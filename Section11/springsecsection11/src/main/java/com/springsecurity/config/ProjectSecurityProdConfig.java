package com.springsecurity.config;


import static org.springframework.security.config.Customizer.withDefaults;

import com.springsecurity.exceptionhandling.CustomAccessDeniedHandler;
import com.springsecurity.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import com.springsecurity.filter.AuthoritiesLoggingAfterFilter;
import com.springsecurity.filter.CsrfCookieFilter;
import com.springsecurity.filter.JWTTokenGeneratorFilter;
import com.springsecurity.filter.JWTTokenValidatorFilter;
import com.springsecurity.filter.RequestValidationBeforeFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Profile("prod")
public class ProjectSecurityProdConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    //This object is used to validate the csrf token we got from the ui is legit or not
    CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

    http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))   //This is done so that spring doesn't create jsession id and store it in the system. We will be using jwt which is stateless
        .cors(corsConfig ->
            corsConfig.configurationSource(new CorsConfigurationSource() {   //Required for cors to allow any request coming from http://localhost:4200, u can put a different host on requirement
              @Override
              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setExposedHeaders(Arrays.asList("Authorization")); //Since jwt token will be sent from backend to frontend, we need to set this to allow to send headers from backend to front
                config.setMaxAge(3600L);
                return config;
              }
            }))
        .csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler) //This csrfTokeRequ...Handler object is used to check the csrf token passed as part of the request is valid or not
            .ignoringRequestMatchers("/contact","/register","/apiLogin") // Won't need csrf for these three
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // This is set to false so that the client can read the csrf cookie, else they cant . This should be set to false
        .addFilterAt(new CsrfCookieFilter(), BasicAuthenticationFilter.class) // This sends the csrf token after the basic authentication is complete in a cookie for a request
        .addFilterBefore(new RequestValidationBeforeFilter(),BasicAuthenticationFilter.class) //This will execute our custom email check filter just before BasicAuthentication filter
        .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class) // Some custom logging filter to show how after filter works, here this will run after basic(account + password) filer
        .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)  //This will generate jwt token on login (would contain the userName + password hashed for jwt) and update the response header with it, this will be used for further requests from the frontend
        .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)  //This will be used to validate the jwt sent from the frontend
        .requiresChannel(rcc->rcc.anyRequest().requiresSecure())  //Allows only https, good for prod envs
        .authorizeHttpRequests((requests) -> requests
        .requestMatchers("/myAccount").hasRole("USER")
            .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/myLoans").hasRole("USER")
            .requestMatchers("/myCards").hasRole("USER")
            .requestMatchers("/user").authenticated()
        .requestMatchers("/notices", "/contact", "/error","/register","/apiLogin").permitAll());
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

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {
    EazyBankUsernamePwdAuthenticationProvider authenticationProvider =
        new EazyBankUsernamePwdAuthenticationProvider(userDetailsService, passwordEncoder);
    ProviderManager providerManager = new ProviderManager(authenticationProvider);
    providerManager.setEraseCredentialsAfterAuthentication(false);
    return  providerManager;
  }


}
