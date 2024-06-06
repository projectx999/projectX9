package com.hydra.divideup.controller;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.hydra.divideup.exception.DivideUpError.USER_NOT_FOUND;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final String usersUrl = "/api/v1/users";

    @Test
    void tetGetUser() throws Exception {
        final String id = "123";
        User user = new User("manji@gmail", "123456789", "pass@123");
        user.setId(id);

        //when
        when(userService.getUser(id)).thenReturn(user);

        //then
        mockMvc.perform(get(usersUrl + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.phoneNumber",is(user.getPhoneNumber())))
                //make sure password not revealed outside
                .andExpect(jsonPath("$.password").doesNotExist());

        //and
        verify(userService, times(1)).getUser(id);
    }

    @Test
    void testGetUser_NotFound() throws Exception {
        final String id = "234";

        //when
        when(userService.getUser(id)).thenThrow(new RecordNotFoundException(USER_NOT_FOUND));

        //then
        mockMvc.perform(get(usersUrl + "/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is(USER_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message", is(USER_NOT_FOUND.getMessage())));
        //And
        verify(userService, times(1)).getUser(id);

    }

    @Test
    void testGetUsers() throws Exception {
        User user1 = new User("manji@gmail", "123456789", "pass@123");
        user1.setId("123");

        User user2 = new User("ranji@gmail", "678912345", "check@123");
        user2.setId("234");

        List<User> users = List.of(user1, user2);

        //when
        when(userService.getUsers()).thenReturn(users);

        //then
        mockMvc.perform(get(usersUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                //user1
                .andExpect(jsonPath("$[0].id", is(user1.getId())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[0].phoneNumber", is(user1.getPhoneNumber())))
                //make sure password not revealed outside
                .andExpect(jsonPath("$[0].password").doesNotExist())
                //user2
                .andExpect(jsonPath("$[1].id", is(user2.getId())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())))
                .andExpect(jsonPath("$[1].phoneNumber", is(user2.getPhoneNumber())))
                //make sure password not revealed outside
                .andExpect(jsonPath("$[1].password").doesNotExist());
        //and
        verify(userService, times(1)).getUsers();

    }

    @Test
    void testGetUsers_EmptyList() throws Exception {
        //when
        when(userService.getUsers()).thenReturn(Collections.emptyList());
        //then
        mockMvc.perform(get(usersUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(0)));

    }

}

