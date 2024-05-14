package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.USER_ALREADY_EXISTS;
import static com.hydra.divideup.exception.DivideUpError.USER_NOT_FOUND;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.exception.RecordAlreadyExistsException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.model.UserDTO;
import com.hydra.divideup.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


  private final UserRepository userRepository;

  private final BCryptPasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(UserDTO user) {
    String encodedPwd = passwordEncoder.encode(user.password());
    User newUser = new User(user.name(), user.email(), user.phone(), encodedPwd);
    List<User> byEmailOrPhoneNumber = userRepository.findByEmailOrPhoneNumber(user.email(),
        user.phone());
    if (!byEmailOrPhoneNumber.isEmpty()) {
      throw new RecordAlreadyExistsException(USER_ALREADY_EXISTS);
    }
    return userRepository.save(newUser);
  }

  public User getUser(String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
  }

  public List<User> getUsers() {
    return userRepository.findAll();
  }

  public User updateUser(String id, User user) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
    List<User> byEmailOrPhoneNumber = userRepository.findByEmailOrPhoneNumber(user.getEmail(),
        user.getPhoneNumber());
    if (!byEmailOrPhoneNumber.isEmpty()) {
      throw new RecordAlreadyExistsException(USER_ALREADY_EXISTS);
    }
    existingUser.setName(user.getName());
    existingUser.setEmail(user.getEmail());
    existingUser.setPhoneNumber(user.getPhoneNumber());
    existingUser.setCountry(user.getCountry());
    existingUser.setDefaultCurrency(user.getDefaultCurrency());
    existingUser.setLanguage(user.getLanguage());
    return userRepository.save(existingUser);
  }

  public User blockUser(String id) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
    existingUser.setBlocked(true);
    return userRepository.save(existingUser);
  }

  public User unblockUser(String id) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
    existingUser.setBlocked(false);
    return userRepository.save(existingUser);
  }

  public User deleteUser(String id) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException(USER_NOT_FOUND));
    existingUser.setDeleted(true);
    return userRepository.save(existingUser);
  }
}
