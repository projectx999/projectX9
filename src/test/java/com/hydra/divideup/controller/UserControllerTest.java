package com.hydra.divideup.controller;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.exception.DivideUpError;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Supplier;

import static com.hydra.divideup.exception.DivideUpError.USER_NOT_FOUND;
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
        mockMvc.perform(get(usersUrl+"/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                //make sure password not revealed
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
        mockMvc.perform(get(usersUrl + "/{id}",id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code",is(USER_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message",is(USER_NOT_FOUND.getMessage())));
        verify(userService,times(1)).getUser(id);

    }

}

