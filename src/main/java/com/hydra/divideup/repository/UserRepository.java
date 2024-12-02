package com.hydra.divideup.repository;

import com.hydra.divideup.entity.User;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  List<User> findByEmailOrPhone(String email, String phoneNumber);
}
