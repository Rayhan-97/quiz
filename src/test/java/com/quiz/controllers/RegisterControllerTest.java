package com.quiz.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.quiz.controllers.dtos.UserDto;
import com.quiz.core.entities.User;
import com.quiz.core.error.ErrorResponse;
import com.quiz.core.repositories.UserRepository;
import com.quiz.launch.Application;
import jakarta.transaction.Transactional;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static com.quiz.core.error.ErrorResponse.*;
import static com.quiz.core.util.ListUtils.convertToList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
@Transactional
class RegisterControllerTest
{
    private static final ObjectWriter JSON_WRITER = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenRequestToRegister_whenHandle_then200() throws Exception
    {
        String email = "email@email.com";
        UserDto userDto = new UserDto("username", email, "password");

        String json = JSON_WRITER.writeValueAsString(userDto);

        List<User> users = convertToList(userRepository.findAll());
        assertThat(users).isEmpty();

        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        users = convertToList(userRepository.findAll());

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo(email);
    }

    @Test
    public void givenRequestToRegisterAndUserWithEmailAlreadyExists_whenHandle_then400BadRequest() throws Exception
    {
        String email = "email@email.com";
        userRepository.save(new User("username", email, "password"));

        UserDto userDtoWithSameEmail = new UserDto("username2", email, "password2");

        String json = JSON_WRITER.writeValueAsString(userDtoWithSameEmail);

        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", is(REGISTRATION_USER_ALREADY_EXISTS_ERROR.body().errorCode())));
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    public void givenRequestToRegisterAndInvalidUser_whenHandle_then400BadRequest(UserDto userDto, ErrorResponse errorResponse) throws Exception
    {
        String json = JSON_WRITER.writeValueAsString(userDto);

        mvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode", is(errorResponse.body().errorCode())))
                .andExpect(jsonPath("$.errorMessage", is(errorResponse.body().errorMessage())));
    }

    private static Stream<Arguments> invalidUsers()
    {
        String invalidUsername = "*&^";
        String invalidEmail = "q@a";
        String invalidPassword = "short";

        return Stream.of(
                arguments(new UserDto(invalidUsername, "email@email.com", "password"), REGISTRATION_USERNAME_ERROR),
                arguments(new UserDto("username", invalidEmail, "password"), REGISTRATION_EMAIL_ERROR),
                arguments(new UserDto("username", "email@email.com", invalidPassword), REGISTRATION_PASSWORD_ERROR)
        );
    }
}