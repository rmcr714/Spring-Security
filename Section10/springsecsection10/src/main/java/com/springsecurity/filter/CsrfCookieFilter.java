package com.springsecurity.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

public class CsrfCookieFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        // Render the token value to a cookie by causing the deferred token to be loaded
        csrfToken.getToken();

        //After the above filter ran we need to continue other filters that will happen in chain
        //If we remove this we won't get any response and it will be blank
        //These filters are like chain e.g. cors run then this csrf filer then the basic authentication
        //(The one which checks the username and password present in db) . So after this custom filer inorder to continue
        //With other filters we need to do this
        filterChain.doFilter(request, response);
    }
}