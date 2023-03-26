package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@Transactional
class UserServiceTest {
    private UserService userService;

    private final User userTest = User
            .builder()
            .id(1L)
            .name("name1")
            .email("email1@ya.ru")
            .build();

    public UserServiceTest(@Autowired UserService userService) {
        this.userService = userService;
    }

    @Test
    void shouldCreateUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(userTest));
        assertThat(userDto.getName(), equalTo(userTest.getName()));
        assertThat(userDto.getEmail(), equalTo(userTest.getEmail()));
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(userTest));
        userDto.setName("user1111");
        userDto.setEmail("user1111@mail.ru");
        UserDto userDtoUpdated = userService.update(userDto);

        assertNotEquals(userDtoUpdated.getName(), "name1");
        assertThat(userDtoUpdated.getName(), equalTo("user1111"));
        assertNotEquals(userDtoUpdated.getEmail(), "email1@ya.ru");
        assertThat(userDtoUpdated.getEmail(), equalTo("user1111@mail.ru"));
    }

    @Test
    void shouldGetAllUsers() {
        User userTest1 = User
                .builder()
                .id(2L)
                .name("name2")
                .email("email2@ya.ru")
                .build();
        UserDto userDto = userService.create(UserMapper.toUserDto(userTest));
        UserDto userDto1 = userService.create(UserMapper.toUserDto(userTest1));

        assertEquals(userService.getAll().size(), 2);
        assertThat(userService.getAll().get(0), equalTo(userDto));
        assertThat(userService.getAll().get(1), equalTo(userDto1));
    }

    @Test
    void shouldGetUserById() {
        UserDto userDto = userService.create(UserMapper.toUserDto(userTest));
        assertEquals(userService.getById(userDto.getId()), userDto);
    }

    @Test
    void shouldDeleteUser() {
        UserDto userDto = userService.create(UserMapper.toUserDto(userTest));
        int size = userService.getAll().size();
        assertEquals(size, 1);
        userService.delete(userDto);
        assertNotEquals(size, userService.getAll().size());
    }

    @Test
    void shouldDeleteUserById() {
        UserDto userDto = userService.create(UserMapper.toUserDto(userTest));
        int size = userService.getAll().size();
        assertEquals(size, 1);
        userService.deleteById(userDto.getId());
        assertNotEquals(size, userService.getAll().size());
    }
}