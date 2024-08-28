package com.quiz.controllers;

import com.quiz.configuration.jwt.JwtTokenGenerator;
import com.quiz.controllers.dtos.UserCredentialsDto;
import com.quiz.core.entities.RefreshToken;
import com.quiz.core.entities.User;
import com.quiz.core.error.ErrorResponse;
import com.quiz.core.repositories.UserRepository;
import com.quiz.services.LoginService;
import com.quiz.services.RefreshTokenService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Optional;

import static com.quiz.core.error.ErrorResponse.REFRESH_TOKEN_COOKIE_MISSING_ERROR;
import static com.quiz.core.error.ErrorResponse.REFRESH_TOKEN_COOKIE_UNKNOWN_ERROR;

@SuppressWarnings("unused")
@Slf4j
@RestController
public class LoginController
{
    @Value("${refreshToken.cookieName}")
    private String REFRESH_TOKEN_COOKIE_NAME;

    @Value("${refreshToken.expirySeconds}")
    private int REFRESH_TOKEN_EXPIRY_SECONDS;

    @Autowired
    Environment env;

    private final LoginService loginService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    public LoginController(
            LoginService loginService,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            JwtTokenGenerator jwtTokenGenerator
    )
    {
        this.loginService = loginService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserCredentialsDto userCredentialsDto)
    {
        String accessToken = loginService.retrieveLoginToken(userCredentialsDto.convert());

        Optional<User> user = userRepository.findByEmail(userCredentialsDto.email());
        assert user.isPresent();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get());

        return buildResponse(refreshToken, accessToken);
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
                log.info("Deleted refresh token for user: {}", email);
            }
        }

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/logout");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request)
    {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);

        if (refreshTokenCookie == null)
        {
            return ResponseEntity.badRequest().body(REFRESH_TOKEN_COOKIE_MISSING_ERROR);
        }

        Optional<RefreshToken> optionalRefreshToken =
                refreshTokenService.findByToken(refreshTokenCookie.getValue());

        if (optionalRefreshToken.isEmpty())
        {
            return ResponseEntity.badRequest().body(REFRESH_TOKEN_COOKIE_UNKNOWN_ERROR);
        }

        RefreshToken refreshToken =
                refreshTokenService.verifyExpirationOrThrow(optionalRefreshToken.get());

        User user = refreshToken.getUser();
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
        String newAccessToken = jwtTokenGenerator.generate(user.getEmail());

        return buildResponse(newRefreshToken, newAccessToken);
    }

    private ResponseEntity<String> buildResponse(RefreshToken refreshToken, String accessToken)
    {
        ResponseCookie responseCookie = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken.getToken())
                .httpOnly(true)
                .maxAge(REFRESH_TOKEN_EXPIRY_SECONDS)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(accessToken);
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

    @ControllerAdvice
    private static class ExpiredRefreshTokenAdvice
    {
        @ResponseBody
        @ExceptionHandler(RefreshTokenService.RefreshTokenExpiredException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        ErrorResponse expiredRefreshTokenHandler(RefreshTokenService.RefreshTokenExpiredException ex)
        {
            return ErrorResponse.REFRESH_TOKEN_COOKIE_EXPIRED_ERROR;
        }
    }
}
