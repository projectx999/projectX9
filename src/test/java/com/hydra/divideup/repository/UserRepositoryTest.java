package com.hydra.divideup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hydra.divideup.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void whenSaveUser_thenUserCanBeRetrieved() {
    User user = new User();
    user.setName("Shan");
    user.setEmail("test@gmail.com");
    userRepository.save(user);
    User found = userRepository.findById(user.getId()).orElse(null);
    assertThat(found)
        .isNotNull()
        .returns("Shan", User::getName)
        .returns("test@gmail.com", User::getEmail);
  }
}
