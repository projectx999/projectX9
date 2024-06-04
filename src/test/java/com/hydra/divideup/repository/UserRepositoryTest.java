package com.hydra.divideup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hydra.divideup.entity.User;
import java.util.List;
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
    user.setName("test");
    user.setEmail("test@gmail.com");
    userRepository.save(user);
    User found = userRepository.findById(user.getId()).orElse(null);
    assertThat(found)
        .isNotNull()
        .returns("test", User::getName)
        .returns("test@gmail.com", User::getEmail);
  }

  @Test
  void whenFindByEmailOrPhoneNumber_thenUserCanBeRetrieved() {
    User user1 = new User();
    user1.setName("test1");
    user1.setEmail("test@gmail.com");
    user1.setPhoneNumber("1234567890");
    userRepository.save(user1);
    User user2 = new User();
    user2.setName("test2");
    user2.setEmail("test2@gmail.com");
    user2.setPhoneNumber("0987654321");
    userRepository.save(user2);
    List<User> found = userRepository.findByEmailOrPhoneNumber("test@gmail.com", "0987654321");
    assertThat(found)
        .isNotNull()
        .hasSize(2)
        .extracting(User::getName)
        .contains("test1", "test2");
    List<User> found1 = userRepository.findByEmailOrPhoneNumber(null, null);
    assertThat(found1)
        .isNotNull()
        .isEmpty();
    List<User> found2 = userRepository.findByEmailOrPhoneNumber("test3@gmail.com", "0987654321");
    assertThat(found2)
        .isNotNull()
        .hasSize(1)
        .extracting(User::getName)
        .contains("test2");
    List<User> found3 = userRepository.findByEmailOrPhoneNumber("test3@gmail.com", "5784675678");
    assertThat(found3)
        .isNotNull()
        .isEmpty();
  }
}
