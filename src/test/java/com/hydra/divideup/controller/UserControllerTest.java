package com.hydra.divideup.controller;

import static com.hydra.divideup.exception.DivideUpError.USER_EMAIL_EXISTS;
import static com.hydra.divideup.exception.DivideUpError.USER_NOT_FOUND;
import static com.hydra.divideup.exception.DivideUpError.USER_PHONE_EXISTS;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hydra.divideup.entity.User;
import com.hydra.divideup.exception.RecordAlreadyExistsException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.model.UserDTO;
import com.hydra.divideup.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @Autowired private ObjectMapper objectMapper;

  private final Supplier<RecordNotFoundException> userNotFoundSupplier =
      () -> new RecordNotFoundException(USER_NOT_FOUND);

  private final String usersUrl = "/api/v1/users";

  @Test
  void tetGetUser() throws Exception {
    // given
    final String id = "123";
    User user = new User("manji@gmail", "123456789", "pass@123");
    user.setId(id);

    // when
    when(userService.getUser(id)).thenReturn(user);

    // then
    mockMvc
        .perform(get(usersUrl + "/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.email", is(user.getEmail())))
        .andExpect(jsonPath("$.phone", is(user.getPhone())))
        // make sure password not revealed outside
        .andExpect(jsonPath("$.password").doesNotExist());

    // and
    verify(userService, times(1)).getUser(id);
  }

  @Test
  void testGetUser_NotFound() throws Exception {
    // given
    final String id = "234";

    // when
    when(userService.getUser(id)).thenThrow(new RecordNotFoundException(USER_NOT_FOUND));

    // then
    mockMvc
        .perform(get(usersUrl + "/{id}", id))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(USER_NOT_FOUND.getCode())))
        .andExpect(jsonPath("$.message", is(USER_NOT_FOUND.getMessage())));
    // And
    verify(userService, times(1)).getUser(id);
  }

  @Test
  void testGetUsers() throws Exception {
    // given
    User user1 = new User("manji@gmail", "123456789", "pass@123");
    user1.setId("123");

    User user2 = new User("ranji@gmail", "678912345", "check@123");
    user2.setId("234");

    List<User> users = List.of(user1, user2);

    // when
    when(userService.getUsers()).thenReturn(users);

    // then
    mockMvc
        .perform(get(usersUrl))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        // user1
        .andExpect(jsonPath("$[0].id", is(user1.getId())))
        .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
        .andExpect(jsonPath("$[0].phone", is(user1.getPhone())))
        // make sure password not revealed outside
        .andExpect(jsonPath("$[0].password").doesNotExist())
        // user2
        .andExpect(jsonPath("$[1].id", is(user2.getId())))
        .andExpect(jsonPath("$[1].email", is(user2.getEmail())))
        .andExpect(jsonPath("$[1].phone", is(user2.getPhone())))
        // make sure password not revealed outside
        .andExpect(jsonPath("$[1].password").doesNotExist());
    // and
    verify(userService, times(1)).getUsers();
  }

  @Test
  void testGetUsers_EmptyList() throws Exception {
    // when
    when(userService.getUsers()).thenReturn(Collections.emptyList());
    // then
    mockMvc.perform(get(usersUrl)).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void testCreateUser() throws Exception {
    // given
    UserDTO userDTO =
        new UserDTO(null, "123456789", "manji@gmail", "pass@123", null, null, null, null);

    final String id = "123";
    User user = new User("manji@gmail", "123456789", "pass@123");
    user.setId(id);

    // when
    when(userService.createUser(any(UserDTO.class))).thenReturn(user);
    // then
    mockMvc
        .perform(
            post(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.email", is(user.getEmail())))
        .andExpect(jsonPath("$.phone", is(user.getPhone())))
        // make sure password not revealed outside
        .andExpect(jsonPath("$.password").doesNotExist());
    // and
    verify(userService, times(1)).createUser(any(UserDTO.class));
  }

  @Test
  void testCreateUser_ExistingEmail() throws Exception {
    // given
    UserDTO existingUser = new UserDTO();
    existingUser.setEmail("existing@example.com");
    // when
    when(userService.createUser(any(UserDTO.class)))
        .thenThrow(new RecordAlreadyExistsException(USER_EMAIL_EXISTS));

    // then
    mockMvc
        .perform(
            post(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingUser)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(USER_EMAIL_EXISTS.getCode())))
        .andExpect(jsonPath("$.message", is(USER_EMAIL_EXISTS.getMessage())));
    // and
    verify(userService, times(1)).createUser(any(UserDTO.class));
  }

  @Test
  void testCreateUser_ExistingPhoneNumber() throws Exception {
    // given
    UserDTO existingUser = new UserDTO();
    existingUser.setPhone("123456789");

    // when
    when(userService.createUser(any(UserDTO.class)))
        .thenThrow(new RecordAlreadyExistsException(USER_PHONE_EXISTS));

    // then
    mockMvc
        .perform(
            post(usersUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingUser)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(USER_PHONE_EXISTS.getCode())))
        .andExpect(jsonPath("$.message", is(USER_PHONE_EXISTS.getMessage())));
    // and
    verify(userService, times(1)).createUser(any(UserDTO.class));
  }

  // @Disabled
  @Test
  void testUpdateUser() throws Exception {
    // given
    final String id = "123";
    UserDTO userDTO = new UserDTO();
    userDTO.setId(id);
    userDTO.setEmail("manji@gmail.com");

    User updatedUser = new User();
    updatedUser.setId("123");
    updatedUser.setPhone("987654321");
    updatedUser.setEmail("updated@example.com");

    // When
    when(userService.updateUser(eq(id), any(UserDTO.class))).thenReturn(updatedUser);

    // Then
    mockMvc
        .perform(
            put(usersUrl + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.phone", is(updatedUser.getPhone())))
        .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));

    // and
    verify(userService, times(1)).updateUser(eq(id), any(UserDTO.class));
  }

  @Test
  void testUpdateUser_UserNotFound() throws Exception {
    // given
    final String id = "123";
    UserDTO userDTO = new UserDTO();
    userDTO.setId(id);
    userDTO.setEmail("manji@gmail");
    userDTO.setPhone("123456789");
    userDTO.setPassword("pass@123");
    // when
    when(userService.updateUser(eq(id), any(UserDTO.class))).thenThrow(userNotFoundSupplier.get());
    // then
    mockMvc
        .perform(
            put(usersUrl + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(USER_NOT_FOUND.getCode())))
        .andExpect(jsonPath("$.message", is(USER_NOT_FOUND.getMessage())));
    // and
    verify(userService, times(1)).updateUser(eq(id), any(UserDTO.class));
  }

  @Test
  void testBlockUser() throws Exception {
    // given
    final String id = "123";
    User blockedUser = new User("manji@gmail", "123456789", "pass@123");
    blockedUser.setBlocked(true);
    blockedUser.setId(id);

    // when
    when(userService.blockUser(id)).thenReturn(blockedUser);

    // then
    mockMvc
        .perform(put(usersUrl + "/block/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.phone", is(blockedUser.getPhone())))
        .andExpect(jsonPath("$.email", is(blockedUser.getEmail())))
        .andExpect(jsonPath("$.blocked", is(true)));
    // and
    verify(userService, times(1)).blockUser(id);
  }

  @Test
  void testBlockUser_userNotFound() throws Exception {
    // given
    final String id = "123";

    // when
    when(userService.blockUser(id)).thenThrow(userNotFoundSupplier.get());

    // then
    mockMvc
        .perform(put(usersUrl + "/block/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(USER_NOT_FOUND.getCode())))
        .andExpect(jsonPath("$.message", is(USER_NOT_FOUND.getMessage())));
    // and
    verify(userService, times(1)).blockUser(id);
  }

  @Test
  void testUnblockUser() throws Exception {
    // given
    final String id = "123";
    User blockedUser = new User("manji@gmail", "123456789", "pass@123");
    blockedUser.setBlocked(false);
    blockedUser.setId(id);

    // when
    when(userService.unblockUser(id)).thenReturn(blockedUser);

    // then
    mockMvc
        .perform(put(usersUrl + "/unblock/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.phone", is(blockedUser.getPhone())))
        .andExpect(jsonPath("$.email", is(blockedUser.getEmail())))
        .andExpect(jsonPath("$.blocked", is(false)));
    // and
    verify(userService, times(1)).unblockUser(id);
  }

  @Test
  void testUnblockUser_userNotFound() throws Exception {
    // given
    final String id = "123";
    // when
    when(userService.unblockUser(id)).thenThrow(userNotFoundSupplier.get());

    // then
    mockMvc
        .perform(put(usersUrl + "/unblock/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(USER_NOT_FOUND.getCode())))
        .andExpect(jsonPath("$.message", is(USER_NOT_FOUND.getMessage())));

    // and
    verify(userService, times(1)).unblockUser(id);
  }

  @Test
  void testDeleteUser() throws Exception {
    // given
    final String id = "123";
    User deletedUser = new User("manji@gmail", "123456789", "pass@123");
    deletedUser.setId(id);
    deletedUser.setDeleted(true);

    // when
    when(userService.deleteUser(id)).thenReturn(deletedUser);
    // then
    mockMvc
        .perform(delete(usersUrl + "/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.phone", is(deletedUser.getPhone())))
        .andExpect(jsonPath("$.email", is(deletedUser.getEmail())))
        .andExpect(jsonPath("$.deleted", is(true)));
    // and
    verify(userService, times(1)).deleteUser(id);
  }

  @Test
  void testDeleteUser_userNotFound() throws Exception {
    // given
    final String id = "123";
    // when
    when(userService.deleteUser(id)).thenThrow(userNotFoundSupplier.get());
    // then
    mockMvc
        .perform(delete(usersUrl + "/{id}", id).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(USER_NOT_FOUND.getCode())))
        .andExpect(jsonPath("$.message", is(USER_NOT_FOUND.getMessage())));

    // and
    verify(userService, times(1)).deleteUser(id);
  }
}
