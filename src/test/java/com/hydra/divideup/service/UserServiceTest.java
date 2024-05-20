package com.hydra.divideup.service;

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

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;


  @InjectMocks
  private UserService userService;

  @Test
  void testCreateUser() {
    // Given
    UserDTO userDTO = new UserDTO("TestUser", "1243", "test@gmail.com", "1234567890");
    User user = new User(userDTO.name(), userDTO.email(), userDTO.phone(), "encodedPassword");

    // When
    when(userRepository.findByEmailOrPhoneNumber(userDTO.email(), userDTO.phone())).thenReturn(
        List.of());
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

    // Then
    User savedUser = userService.createUser(userDTO);
    assertEquals(user, savedUser);
  }

  @Test
  void testCreateUserAlreadyExists() {
    // Given
    UserDTO userDTO = new UserDTO("TestUser", "1243", "test@gmail.com", "1234567890");
    User existingUser = new User(userDTO.name(), userDTO.email(), userDTO.phone(),
        userDTO.password());

    // When
    when(userRepository.findByEmailOrPhoneNumber(userDTO.email(), userDTO.phone())).thenReturn(
        List.of(existingUser));

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
    User user2 = new User();
    user2.setId("testId2");
    user2.setName("TestUser2");
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
    User updatedUser = new User();
    updatedUser.setId("testId");
    updatedUser.setName("UpdatedUser");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(user));
    when(userRepository.findByEmailOrPhoneNumber(updatedUser.getEmail(),
        updatedUser.getPhoneNumber()))
        .thenReturn(List.of());
    when(userRepository.save(user)).thenReturn(updatedUser);
    User savedUser = userService.updateUser("testId", updatedUser);
    // Then
    assertEquals(updatedUser, savedUser);
  }

  @Test
  void testUpdateUserNotFound() {
    //Given
    var user = new User();
    user.setId("testId");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.empty());
    // Then
    assertThrows(RecordNotFoundException.class, () -> userService.updateUser("testId", user));
  }

  @Test
  void testUpdateUserEmailOrPhoneNumberAlreadyExists() {
    // Given
    User user = new User();
    user.setId("testId");
    user.setName("TestUser");
    User user1 = new User();
    user1.setId("testId1");
    // When
    when(userRepository.findById("testId")).thenReturn(Optional.of(user));
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
