package com.quiz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.quiz.controllers.dtos.UserCredentialsDto;
import com.quiz.controllers.dtos.UserDto;
import com.quiz.core.error.ErrorResponse;
import com.quiz.launch.Application;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
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
@TestPropertySource(properties = {"refreshToken.expirySeconds: 0"})
class ExpiredRefreshTokenTest
{
    private static final ObjectWriter JSON_WRITER =
            new ObjectMapper().writer().withDefaultPrettyPrinter();

    private static final String LOGIN_URL = "/login";
    private static final String REFRESH_URL = "/refresh";

    private static final UserDto USER =
            new UserDto("username", "user@email.com", "password");

    @Value("${refreshToken.cookieName}")
    private String refreshTokenCookieName;

    @Autowired
    private MockMvc mvc;


    @BeforeEach
    void setUp() throws Exception
    {
        registerUser();
    }

    @Test
    void givenRequestToRefresh_whenExpiredRefreshToken_thenBadRequestResponse() throws Exception
    {
        ErrorResponse.ErrorResponseBody expectedError = ErrorResponse.REFRESH_TOKEN_COOKIE_EXPIRED_ERROR.body();

        Cookie refreshTokenCookie = loginUser().refreshTokenCookie();

        mvc.perform(get(REFRESH_URL).cookie(refreshTokenCookie))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", is(expectedError.errorCode())))
                .andExpect(jsonPath("$.errorMessage", is(expectedError.errorMessage())))
                .andReturn();
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