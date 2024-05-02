package com.hydra.divideup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hydra.divideup.entity.User;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  void whenSaveUser_thenUserCanBeRetrieved() {
    User user = new User();
    user.setName("Shan");
    user.setEmail("test@gmail.com");
    userRepository.save(user);
    User found = userRepository.findById(user.getId()).orElse(null);
    assertThat(found)
            .isNotNull()
            .returns("Shan",User::getName)
            .returns("test@gmail.com",User::getEmail);
  }

}
