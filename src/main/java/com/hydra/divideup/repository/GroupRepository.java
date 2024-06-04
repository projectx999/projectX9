package com.hydra.divideup.repository;

import com.hydra.divideup.entity.Group;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

  List<Group> findByMembersContains(String member);

}
