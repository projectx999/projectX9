package com.hydra.divideup.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.hydra.divideup.entity.Group;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("test")
class GroupRepositoryTest {

  @Autowired
  private GroupRepository groupRepository;

  @BeforeEach
  public void setUp() {
    groupRepository.deleteAll();
  }

  @Test
  void testFindByMembersContains() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(Set.of("test1", "test2"));
    groupRepository.save(group);

    Group group1 = new Group();
    group1.setGroupName("Test Group 1");
    group1.setMembers(Set.of("test1", "test3"));
    groupRepository.save(group1);

    Group group2 = new Group();
    group2.setGroupName("Test Group 2");
    group2.setMembers(Set.of("test3", "test4"));
    groupRepository.save(group2);

    // When
    List<Group> groups = groupRepository.findByMembersContains("test1");

    // Then
    assertEquals(2, groups.size());

    // When
    groups = groupRepository.findByMembersContains("test4");

    // Then
    assertEquals(1, groups.size());

    // When
    groups = groupRepository.findByMembersContains("test5");

    // Then
    assertEquals(0, groups.size());

  }

}
