package com.quiz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.quiz.configuration.jwt.JwtTokenParser;
import com.quiz.controllers.dtos.UserCredentialsDto;
import com.quiz.controllers.dtos.UserDto;
import com.quiz.core.error.ErrorResponse;
import com.quiz.launch.Application;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.quiz.core.error.ErrorResponse.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
    private static final ObjectWriter JSON_WRITER = new ObjectMapper().writer().withDefaultPrettyPrinter();

    private static final UserDto USER = new UserDto("username", "user@email.com", "password");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtTokenParser jwtTokenParser;

    @BeforeEach
    void setUp() throws Exception
    {
        registerUser(USER);
    }

    @Test
    public void givenRequestToLogin_whenHandle_then200() throws Exception
    {
        String email = USER.email();

        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(email, USER.password());
        String json = JSON_WRITER.writeValueAsString(userCredentialsDto);

        MvcResult mvcResult = mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String jwtToken = mvcResult.getResponse().getContentAsString();

        String parsedEmail = jwtTokenParser.getEmail(jwtToken);

        assertThat(parsedEmail).isEqualTo(email);
    }

    @ParameterizedTest
    @MethodSource("invalidPayloads")
    public void givenRequestToLoginAndInvalidCredentials_whenHandle_then400BadRequest(UserCredentialsDto userCredentialsDto, ErrorResponse errorResponse) throws Exception
    {
        String json = JSON_WRITER.writeValueAsString(userCredentialsDto);

        mvc.perform(post("/login")
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

    private void registerUser(UserDto userDto) throws Exception
    {
        String json = JSON_WRITER.writeValueAsString(userDto);
        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
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