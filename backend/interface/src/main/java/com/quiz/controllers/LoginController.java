package com.quiz.controllers;

import com.quiz.controllers.dtos.UserCredentialsDto;
import com.quiz.core.entities.RefreshToken;
import com.quiz.core.entities.User;
import com.quiz.core.error.ErrorResponse;
import com.quiz.core.repositories.UserRepository;
import com.quiz.services.LoginService;
import com.quiz.services.RefreshTokenService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("unused")
@Slf4j
@RestController
public class LoginController
{
    static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";
    static final int REFRESH_TOKEN_EXPIRY_SECONDS = 24 * 60 * 60;

    private final LoginService loginService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public LoginController(LoginService loginService, UserRepository userRepository, RefreshTokenService refreshTokenService)
    {
        this.loginService = loginService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserCredentialsDto userCredentialsDto)
    {
        String accessToken = loginService.retrieveLoginToken(userCredentialsDto.convert());

        Optional<User> user = userRepository.findByEmail(userCredentialsDto.email());
        assert user.isPresent();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get());
        ResponseCookie responseCookie = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken.getToken())
                .httpOnly(true)
                .maxAge(REFRESH_TOKEN_EXPIRY_SECONDS)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(accessToken);
    }

    @GetMapping("/signout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void signOut(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException, ServletException
    {
        String email = userDetails != null ? userDetails.getUsername() : null;

        if (email != null)
        {
            boolean didDeleteRefreshToken = refreshTokenService.deleteByUserDetails(email);
            if (didDeleteRefreshToken)
            {
                log.info("Deleted refresh token for user: %s".formatted(email));
            }
        }

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/logout");
        requestDispatcher.forward(request, response);
    }

    @ControllerAdvice
    private static class BadCredentialsAdvice
    {
        @ResponseBody
        @ExceptionHandler(BadCredentialsException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        ErrorResponse badCredentialsHandler(BadCredentialsException ex)
        {
            return ErrorResponse.LOGIN_BAD_CREDENTIALS_ERROR;
        }
    }
}
