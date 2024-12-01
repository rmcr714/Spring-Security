package com.springsecurity.config;


import com.springsecurity.model.Customer;
import com.springsecurity.repository.CustomerRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EazyBankUserDetailsService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Customer customer = customerRepository.findByEmail(username).orElseThrow(() -> new
        UsernameNotFoundException("User details not found for the user: " + username));
    List<GrantedAuthority> authorities = customer.getAuthorities().stream().map(authority -> new
        SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
    return new User(customer.getEmail(), customer.getPwd(), authorities);
  }

}
