package com.quiz.configuration;

import com.quiz.configuration.jwt.JwtTokenParser;
import com.quiz.configuration.jwt.JwtTokenValidator;
import com.quiz.configuration.jwt.JwtTokenValidator.JwtTokenValidationResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final UserDetailsService userDetailsService;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtTokenParser jwtTokenParser;

    public JwtAuthenticationFilter(
            UserDetailsService userDetailsService,
            JwtTokenValidator jwtTokenValidator,
            JwtTokenParser jwtTokenParser
    )
    {
        this.userDetailsService = userDetailsService;
        this.jwtTokenValidator = jwtTokenValidator;
        this.jwtTokenParser = jwtTokenParser;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException
    {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer "))
        {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        JwtTokenValidationResult validationResult = jwtTokenValidator.validate(token);
        if (!validationResult.success())
        {
            chain.doFilter(request, response);
            return;
        }

        // Get user identity and set it on the spring security context
        String email = jwtTokenParser.getEmail(token);
        Optional<UserDetails> userDetails = Optional.ofNullable(userDetailsService.loadUserByUsername(email));

        UsernamePasswordAuthenticationToken authentication = userDetails
                .map(user -> authenticated(user, user.getPassword(), user.getAuthorities()))
                .orElseGet(() -> unauthenticated(null, null));

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private static boolean isEmpty(String header)
    {
        return header == null || header.isEmpty();
    }
}
