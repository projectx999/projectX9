package com.hydra.divideup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.exception.RecordAlreadyExistsException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.model.UserDTO;
import com.hydra.divideup.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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
    User user = new User(userDTO.getEmail(), userDTO.getPhoneNumber(), "encodedPassword");

    // When
    when(userRepository.findByEmailOrPhoneNumber(userDTO.getEmail(), userDTO.getPhoneNumber()))
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
    User existingUser =
        new User(userDTO.getEmail(), userDTO.getPhoneNumber(), userDTO.getPassword());

    // When
    when(userRepository.findByEmailOrPhoneNumber(userDTO.getEmail(), userDTO.getPhoneNumber()))
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
    user1.setPhoneNumber("1234567890");
    User user2 = new User();
    user2.setId("testId2");
    user2.setName("TestUser2");
    user2.setEmail("testuser2@mail.com");
    user2.setPhoneNumber("1234567891");
    // When
    when(userRepository.findAll()).thenReturn(List.of(user1, user2));
    // Then
    assertEquals(List.of(user1, user2), userService.getUsers());
  }

  @Test
  void testUpdateUser() {
    // Given
    User user = new User();
    user.setId("testId");
    user.setName("TestUser");
    user.setEmail("testuser1@mail.com");
    user.setPhoneNumber("1234567890");

    UserDTO userDTO = new UserDTO();
    userDTO.setId("testId");
    userDTO.setName("UpdatedUser");
    userDTO.setEmail("updateduser@mail.com");
    userDTO.setPhoneNumber("1234567891");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(user));
    when(userRepository.findByEmailOrPhoneNumber(userDTO.getEmail(), userDTO.getPhoneNumber()))
        .thenReturn(List.of());

    user.setPhoneNumber(userDTO.getPhoneNumber());
    when(userRepository.save(any(User.class))).thenReturn(user);
    User savedUser = userService.updateUser("testId", userDTO);
    // Then
    assertThat(userDTO).usingRecursiveComparison().isEqualTo(savedUser);
  }

  @Test
  void testUpdateUserNotFound() {
    // Given
    var user = new UserDTO();
    user.setId("testId");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.empty());
    // Then
    assertThrows(RecordNotFoundException.class, () -> userService.updateUser("testId", user));
  }

  @Test
  void testUpdateUserEmailOrPhoneNumberAlreadyExists() {
    // Given
    UserDTO user = new UserDTO();
    user.setId("testId");
    user.setName("TestUser");

    User user1 = new User();
    user1.setId("testId1");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(user1));
    when(userRepository.findByEmailOrPhoneNumber(user.getEmail(), user.getPhoneNumber()))
        .thenReturn(List.of(user1));
    // Then
    assertThrows(Exception.class, () -> userService.updateUser("testId", user));
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
