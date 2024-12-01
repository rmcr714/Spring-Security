package com.springsecurity.controller;

import com.springsecurity.constants.ApplicationConstants;
import com.springsecurity.model.Customer;
import com.springsecurity.model.LoginRequestDTO;
import com.springsecurity.model.LoginResponseDTO;
import com.springsecurity.repository.CustomerRepository;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final Environment env;

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody Customer customer) {
    try {
      String hashPwd = passwordEncoder.encode(customer.getPwd());
      customer.setPwd(hashPwd);
      Customer savedCustomer = customerRepository.save(customer);

      if (savedCustomer.getId() > 0) {
        return ResponseEntity.status(HttpStatus.CREATED).
            body("Given user details are successfully registered");
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
            body("User registration failed");
      }
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
          body("An exception occurred: " + ex.getMessage());
    }

  }

  @RequestMapping("/user")
  public Customer getUserDetailsAfterLogin(Authentication authentication) {
    Optional<Customer> optionalCustomer = customerRepository.findByEmail(authentication.getName());
    return optionalCustomer.orElse(null);
  }



  /**
   * This method will be used to get the authentication jwt , we already do that for /user request
   * But in applications there is always need to have a separate method that we could call to get the
   * Jwt token and authenticating ourselves by passing userName and passWord in the request.
   * */

  @PostMapping("/apiLogin")
  public ResponseEntity<LoginResponseDTO> apiLogin (@RequestBody LoginRequestDTO loginRequest) {
    String jwt = "";
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
        loginRequest.password());

    //We will be using a new bean of authentication Manager and then callig the authenticate method
    //of EazyBankUserNamePwdAuthen...provider, this will check in user table for username and password and set
    //the authentication object to true if success else false
    Authentication authenticationResponse = authenticationManager.authenticate(authentication);
    if(null != authenticationResponse && authenticationResponse.isAuthenticated()) {
      if (null != env) {
        //Same jwt generation code
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
            ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        jwt = Jwts.builder().issuer("Eazy Bank").subject("JWT Token")
            .claim("username", authenticationResponse.getName())
            .claim("authorities", authenticationResponse.getAuthorities().stream().map(
                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
            .issuedAt(new java.util.Date())
            .expiration(new java.util.Date((new java.util.Date()).getTime() + 30000000))
            .signWith(secretKey).compact();
      }
    }
    return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
        .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
  }

}

