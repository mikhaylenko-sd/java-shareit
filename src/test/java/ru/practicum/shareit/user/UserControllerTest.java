package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserValidationService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
class UserControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    private UserValidationService userValidationService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDtoTest = UserDto.builder()
            .id(1L)
            .name("name1")
            .email("email1@ya.ru")
            .build();

    private final List<UserDto> listUserDto = new ArrayList<>();

    private static final String X_HEADER = "X-Sharer-User-Id";


    @Test
    void testFindAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(userDtoTest));
        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(userDtoTest.getId()), Long.class))
                .andExpect(jsonPath("$.[0].email", is(userDtoTest.getEmail()), String.class))
                .andExpect(jsonPath("$.[0].name", is(userDtoTest.getName()), String.class));
    }

    @Test
    void testFindUserById() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(userDtoTest);
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header(X_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDtoTest.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDtoTest.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDtoTest.getName()), String.class));
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("name1")
                .email("email1@ya.ru")
                .build();
        when(userService.create(userDto))
                .thenReturn(userDtoTest);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDtoTest.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDtoTest.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDtoTest.getName()), String.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.update(userDtoTest))
                .thenReturn(userDtoTest);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(userDtoTest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDtoTest.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userDtoTest.getEmail()), String.class))
                .andExpect(jsonPath("$.name", is(userDtoTest.getName()), String.class));
    }

    @Test
    void testRemoveUserById() throws Exception {
        mvc.perform(delete("/users/1")
                        .header(X_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    void testHandleUserValidationExceptionWhenCreate() throws Exception {
        UserDto userDto = UserDto
                .builder()
                .id(1L)
                .email("user1111.ru")
                .name("name1111")
                .build();
        doThrow(new ValidationException("Ошибка валидации. Проверьте корректность адреса электронной почты.")).when(userValidationService).validateUserCreate(userDto);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка при выполнении программы"), String.class))
                .andExpect(jsonPath("$.description", is("Ошибка валидации. Проверьте корректность адреса электронной почты."), String.class));
    }

    @Test
    void testHandleUserValidationExceptionWhenUpdate() throws Exception {
        UserDto userDto = UserDto
                .builder()
                .id(1L)
                .email("user1111.ru")
                .name("name1111")
                .build();
        doThrow(new ValidationException("Ошибка валидации. Имя пользователя не может быть пустым.")).when(userValidationService).validateUserUpdate(userDto);
        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER, 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Ошибка при выполнении программы"), String.class))
                .andExpect(jsonPath("$.description", is("Ошибка валидации. Имя пользователя не может быть пустым."), String.class));
    }
}