package com.quiz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.quiz.configuration.jwt.JwtTokenParser;
import com.quiz.controllers.dtos.UserCredentialsDto;
import com.quiz.controllers.dtos.UserDto;
import com.quiz.core.error.ErrorResponse;
import com.quiz.launch.Application;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.quiz.core.error.ErrorResponse.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
@Transactional
class LoginControllerTest
{
    private static final ObjectWriter JSON_WRITER =
            new ObjectMapper().writer().withDefaultPrettyPrinter();

    private static final String LOGIN_URL = "/login";
    private static final String REFRESH_URL = "/refresh";

    private static final UserDto USER =
            new UserDto("username", "user@email.com", "password");

    @Value("${refreshToken.cookieName}")
    private String refreshTokenCookieName;

    @Value("${refreshToken.expirySeconds}")
    private int refreshTokenExpirySeconds;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenParser jwtTokenParser;


    @Nested
    class LoginTests
    {
        @BeforeEach
        void setUp() throws Exception
        {
            registerUser();
        }

        @Test
        public void givenRequestToLogin_whenHandle_then200() throws Exception
        {
            LoginResult loginResult = loginUser();

            Cookie cookie = loginResult.refreshTokenCookie();
            assertThat(cookie).isNotNull();
            assertThat(cookie.getValue()).isNotBlank();
            assertThat(cookie.getMaxAge()).isEqualTo(refreshTokenExpirySeconds);

            String jwtToken = loginResult.jwt();
            String parsedEmail = jwtTokenParser.getEmail(jwtToken);
            assertThat(parsedEmail).isEqualTo(USER.email());
        }

        @ParameterizedTest
        @MethodSource("invalidPayloads")
        public void givenRequestToLoginAndInvalidCredentials_whenHandle_then400BadRequest(
                UserCredentialsDto userCredentialsDto, ErrorResponse errorResponse
        ) throws Exception
        {
            String json = JSON_WRITER.writeValueAsString(userCredentialsDto);

            mvc.perform(post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", is(errorResponse.body().errorCode())))
                    .andExpect(jsonPath("$.errorMessage", is(errorResponse.body().errorMessage())));
        }

        private static Stream<Arguments> invalidPayloads()
        {
            String invalidEmail = "q@a";
            String invalidPassword = "short";

            return Stream.of(
                    arguments(new UserCredentialsDto(invalidEmail, "password"), LOGIN_EMAIL_ERROR),
                    arguments(new UserCredentialsDto("email@email.com", invalidPassword), LOGIN_PASSWORD_ERROR),
                    arguments(new UserCredentialsDto("different@email.com", USER.password()), LOGIN_BAD_CREDENTIALS_ERROR),
                    arguments(new UserCredentialsDto(USER.email(), "different password"), LOGIN_BAD_CREDENTIALS_ERROR)
            );
        }
    }

    @Nested
    class LogoutTests
    {
        @BeforeEach
        void setUp() throws Exception
        {
            registerUser();
        }

        @Test
        void givenRequestToLogout_whenRefreshTokenSupplied_thenRefreshTokenCookieUnset() throws Exception
        {
            LoginResult loginResult = loginUser();

            MvcResult mvcResult = mvc.perform(
                            get("/signout")
                                    .header("Authorization", "Bearer " + loginResult.jwt()))
                    .andExpect(cookie().doesNotExist(refreshTokenCookieName))
                    .andExpect(forwardedUrl("/logout"))
                    .andExpect(status().isAccepted())
                    .andReturn();

            String forwardedUrl = mvcResult.getResponse().getForwardedUrl();
            assertThat(forwardedUrl).isNotNull();
            assertThat(forwardedUrl).isEqualTo("/logout");

            MockHttpServletResponse logoutResponse = mvc.perform(get(forwardedUrl))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse();

            Cookie refreshTokenCookie = logoutResponse.getCookie(refreshTokenCookieName);
            assertThat(refreshTokenCookie).isNotNull();
            assertThat(refreshTokenCookie.getValue()).isNull();
            assertThat(refreshTokenCookie.getMaxAge()).isZero();

            String logoutResponseMessage = logoutResponse.getContentAsString();
            assertThat(logoutResponseMessage).contains("You have been successfully logged out");
        }
    }

    @Nested
    class RefreshTokenTests
    {
        @BeforeEach
        void setUp() throws Exception
        {
            registerUser();
        }

        @Test
        void givenRequestToRefresh_whenValidToken_thenReturnsNewTokenInCookieAndNewJwt() throws Exception
        {
            LoginResult loginResult = loginUser();
            String jwt = loginResult.jwt();
            Cookie refreshTokenCookie = loginResult.refreshTokenCookie();

            // wait so jwt generation uses new time for different secret
            Thread.sleep(1_000);

            MvcResult mvcResult = mvc.perform(get(REFRESH_URL).cookie(refreshTokenCookie))
                    .andExpect(status().isOk())
                    .andReturn();

            Cookie newRefreshTokenCookie = mvcResult
                    .getResponse()
                    .getCookie(refreshTokenCookieName);

            assertThat(newRefreshTokenCookie).isNotNull();
            assertThat(newRefreshTokenCookie.getValue()).isNotBlank();
            assertThat(newRefreshTokenCookie).isNotEqualTo(refreshTokenCookie);

            String newJwtToken = mvcResult.getResponse().getContentAsString();

            String parsedEmail = jwtTokenParser.getEmail(newJwtToken);
            assertThat(parsedEmail).isEqualTo(USER.email());
            assertThat(newJwtToken).isNotEqualTo(jwt);
        }

        @Test
        void givenRequestToRefresh_withoutRefreshTokenCookie_thenBadRequestResponse() throws
                Exception
        {
            ErrorResponseBody expectedError = REFRESH_TOKEN_COOKIE_MISSING_ERROR.body();

            mvc.perform(get(REFRESH_URL))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", is(expectedError.errorCode())))
                    .andExpect(jsonPath("$.errorMessage", is(expectedError.errorMessage())))
                    .andReturn();
        }

        @Test
        void givenRequestToRefresh_whenUnknownToken_thenBadRequestResponse() throws
                Exception
        {
            ErrorResponseBody expectedError = REFRESH_TOKEN_COOKIE_UNKNOWN_ERROR.body();

            Cookie unknownToken = new Cookie(refreshTokenCookieName, "unknownToken");
            mvc.perform(get(REFRESH_URL).cookie(unknownToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.errorCode", is(expectedError.errorCode())))
                    .andExpect(jsonPath(
                            "$.errorMessage",
                            is(expectedError.errorMessage())
                    ))
                    .andReturn();
        }
    }


    private void registerUser() throws Exception
    {
        String json = JSON_WRITER.writeValueAsString(USER);
        mvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    private LoginResult loginUser() throws Exception
    {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(
                USER.email(),
                USER.password()
        );
        String json = JSON_WRITER.writeValueAsString(userCredentialsDto);

        MockHttpServletResponse response = mvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        return new LoginResult(
                response.getCookie(refreshTokenCookieName),
                response.getContentAsString()
        );
    }

    private record LoginResult(Cookie refreshTokenCookie, String jwt)
    {
    }
}