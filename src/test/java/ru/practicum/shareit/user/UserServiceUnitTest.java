package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    private final UserDto userDtoTest = UserDto
            .builder()
            .id(1L)
            .name("name1")
            .email("email1@ya.ru")
            .build();

    private final User userTest = User
            .builder()
            .id(1L)
            .name("name1")
            .email("email1@ya.ru")
            .build();

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldReturnEmptyUsersList() {
        when(userRepository.findAll())
                .thenReturn(List.of());
        assertEquals(userService.getAll().size(), 0);
    }

    @Test
    void shouldReturnAllUsers() {
        when(userRepository.save(any()))
                .thenReturn(userTest);
        when(userRepository.findAll())
                .thenReturn(List.of(userTest));

        userService.create(userDtoTest);
        List<UserDto> userDtos = userService.getAll();

        assertEquals(userDtos.size(), 1);
        assertEquals(userDtos.get(0).getId(), userTest.getId());
        assertEquals(userDtos.get(0).getName(), userTest.getName());
        assertEquals(userDtos.get(0).getEmail(), userTest.getEmail());
    }

    @Test
    void shouldGetUserById() {
        when(userRepository.save(any()))
                .thenReturn(userTest);
        when(userRepository.findById(userTest.getId()))
                .thenReturn(Optional.of(userTest));
        userService.create(userDtoTest);

        UserDto userDto = userService.getById(userTest.getId());
        assertEquals(userDto.getId(), userDtoTest.getId());
        assertEquals(userDto.getName(), userDtoTest.getName());
        assertEquals(userDto.getEmail(), userDtoTest.getEmail());
    }

    @Test
    void shouldExceptionWhenGetUserByWrongId() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getById(2L));
    }

    @Test
    void shouldCreateUsers() {
        when(userRepository.save(any()))
                .thenReturn(userTest);
        when(userRepository.findById(userTest.getId()))
                .thenReturn(Optional.of(userTest));
        userService.create(userDtoTest);

        assertEquals(userService.getById(userTest.getId()), userDtoTest);
    }

    @Test
    void shouldExceptionWhenUpdateNotExistUser() {
        userDtoTest.setId(null);
        assertThrows(IllegalArgumentException.class, () -> userService.update(userDtoTest));
    }

    @Test
    void shouldExceptionWhenUpdateUserIfWrongId() {
        when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.update(userDtoTest));
    }

    @Test
    void shouldUpdateUserByName() {
        when(userRepository.findById(userTest.getId()))
                .thenReturn(Optional.of(userTest));
        when(userRepository.save(any()))
                .thenReturn(userTest);

        assertThat(userService.create(userDtoTest).getName(), equalTo("name1"));
        userTest.setName("I am user");
        userDtoTest.setName("I am user");
        assertThat(userService.update(userDtoTest).getName(), equalTo("I am user"));
    }

    @Test
    void shouldUpdateUserByEmail() {
        when(userRepository.findById(userTest.getId()))
                .thenReturn(Optional.of(userTest));
        when(userRepository.save(any()))
                .thenReturn(userTest);

        assertThat(userService.create(userDtoTest).getEmail(), equalTo("email1@ya.ru"));
        userTest.setEmail("iamuser@mail.ru");
        userDtoTest.setEmail("iamuser@mail.ru");
        assertThat(userService.update(userDtoTest).getEmail(), equalTo("iamuser@mail.ru"));
    }

    @Test
    void shouldDeleteUser() {
        assertDoesNotThrow(() -> userService.delete(userDtoTest));
    }

    @Test
    void shouldDeleteUserById() {
        assertDoesNotThrow(() -> userService.deleteById(userDtoTest.getId()));
    }
}