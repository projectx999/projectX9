package com.hydra.divideup.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hydra.divideup.converter.UserConverter;
import com.hydra.divideup.entity.User;
import com.hydra.divideup.exception.RecordAlreadyExistsException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.model.UserDTO;
import com.hydra.divideup.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @Test
  void testCreateUser() {
    // Given
    UserDTO userDTO = new UserDTO("1243", "test@gmail.com", "1234567890");
    User user = new User(userDTO.getEmail(), userDTO.getPhone(), "encodedPassword");

    // When
    when(userRepository.findByEmailOrPhone(userDTO.getEmail(), userDTO.getPhone()))
        .thenReturn(List.of());
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    // Then
    User savedUser = userService.createUser(userDTO);
    assertEquals(user, savedUser);
  }

  @Test
  void testCreateUserAlreadyExists() {
    // Given
    UserDTO userDTO = new UserDTO("1243", "test@gmail.com", "1234567890");
    User existingUser = new User(userDTO.getEmail(), userDTO.getPhone(), userDTO.getPassword());

    // When
    when(userRepository.findByEmailOrPhone(userDTO.getEmail(), userDTO.getPhone()))
        .thenReturn(List.of(existingUser));

    // Then
    assertThrows(RecordAlreadyExistsException.class, () -> userService.createUser(userDTO));
  }

  @Test
  void testGetUserById() {
    // Given
    User user = new User();
    user.setId("testId");
    user.setName("TestUser");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(user));
    User foundUser = userService.getUser("testId");
    // Then
    assertEquals(user, foundUser);
  }

  @Test
  void testGetUserByIdNotFound() {
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.empty());
    // Then
    assertThrows(RecordNotFoundException.class, () -> userService.getUser("testId"));
  }

  @Test
  void testGetUsers() {
    // Given
    User user1 = new User();
    user1.setId("testId1");
    user1.setName("TestUser1");
    user1.setEmail("testuser1@mail.com");
    user1.setPhone("1234567890");
    User user2 = new User();
    user2.setId("testId2");
    user2.setName("TestUser2");
    user2.setEmail("testuser2@mail.com");
    user2.setPhone("1234567891");
    // When
    when(userRepository.findAll()).thenReturn(List.of(user1, user2));
    // Then
    assertEquals(List.of(user1, user2), userService.getUsers());
  }

  @Test
  void testUpdateUser() {
    // Given
    UserDTO userDTO = new UserDTO();
    userDTO.setId("testId");
    userDTO.setName("TestUser");
    userDTO.setEmail("testuser@mail.com");
    userDTO.setPhone("1234567890");
    // db user name and phone are different and so updated
    User dbUser = new User();
    dbUser.setId("testId");
    dbUser.setName("UpdatedUser");
    dbUser.setEmail("testuser@mail.com");
    dbUser.setPhone("1234567891");

    User updatedUser = UserConverter.convertToEntity(userDTO);
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(dbUser));
    when(userRepository.findByEmailOrPhone(updatedUser.getEmail(), updatedUser.getPhone()))
        .thenReturn(List.of());
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    User savedUser = userService.updateUser("testId", userDTO);
    // Then
    assertEquals(updatedUser, savedUser);
  }

  @Test
  void testUpdateUserNotFound() {
    // Given
    var userDTO = new UserDTO();
    userDTO.setId("testId");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.empty());
    // Then
    assertThrows(RecordNotFoundException.class, () -> userService.updateUser("testId", userDTO));
  }

  @Test
  @DisplayName("Do not allow to update with email that already used for other user")
  void testUpdateUserByEmailAlreadyInDB() {
    // Given
    UserDTO userDTO = new UserDTO();
    userDTO.setId("testId");
    userDTO.setName("TestUser");
    userDTO.setPhone("123456789");
    userDTO.setEmail("exsting@mail.com");

    User userByGivenId = new User("user@mail.com", userDTO.getPhone(), "pass");
    userByGivenId.setId("userId");

    User userInDBbyEmailOrPhone = new User("exsting@mail.com", userDTO.getPhone(), "pass2");
    userByGivenId.setId("userId2");

    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(userByGivenId));
    when(userRepository.findByEmailOrPhone(any(), any()))
        .thenReturn(List.of(userInDBbyEmailOrPhone));

    // Then
    assertThrows(
        RecordAlreadyExistsException.class, () -> userService.updateUser("testId", userDTO));
  }

  @Test
  @DisplayName("Do not allow to update with phone that already used for other user")
  void testUpdateUserByPhoneAlreadyInDB() {
    // Given
    UserDTO userDTO = new UserDTO();
    userDTO.setId("testId");
    userDTO.setName("TestUser");
    userDTO.setPhone("90909090");
    userDTO.setEmail("user@mail.com");

    User userByGivenId = new User(userDTO.getEmail(), "123456789", "pass");
    userByGivenId.setId("userId");

    User userInDBbyEmailOrPhone = new User(userDTO.getEmail(), "90909090", "pass2");
    userByGivenId.setId("userId2");

    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(userByGivenId));
    when(userRepository.findByEmailOrPhone(any(), any()))
        .thenReturn(List.of(userInDBbyEmailOrPhone));

    // Then
    assertThrows(
        RecordAlreadyExistsException.class, () -> userService.updateUser("testId", userDTO));
  }

  @Test
  void testBlockUser() {
    // Given
    User user = new User();
    user.setId("testId");
    user.setName("TestUser");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    User blockUser = userService.blockUser("testId");
    // Then
    assertTrue(blockUser.isBlocked());
  }

  @Test
  void testDeleteUser() {
    // Given
    User user = new User();
    user.setId("testId");
    user.setName("TestUser");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    User deletedUser = userService.deleteUser("testId");
    // Then
    assertTrue(deletedUser.isDeleted());
  }
}
