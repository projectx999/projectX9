package com.hydra.divideup.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.repository.GroupRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  @Mock
  private GroupRepository groupRepository;

  @InjectMocks
  private GroupService groupService;

  @Test
  void testGetGroupsByUser() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(Set.of("test1", "test2"));

    // When
    groupService.getGroupsByUser("test1");

    // Then
    verify(groupRepository).findByMembersContains("test1");
  }

  @Test
  void testGetGroup() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(Set.of("test1", "test2"));

    when(groupRepository.findById("test1")).thenReturn(Optional.of(group));
    // When
    groupService.getGroup("test1");
    // Then
    verify(groupRepository).findById("test1");
  }

  @Test
  void testNoGroupFoundGetGroup() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(Set.of("test1", "test2"));

    when(groupRepository.findById("test1")).thenReturn(Optional.empty());
    // When
    assertThrows(RecordNotFoundException.class, () -> groupService.getGroup("test1"));
    // Then
    verify(groupRepository).findById("test1");
  }

  @Test
  void testCreateGroup() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(new HashSet<>(Set.of("test1", "test2")));
    group.setCreatedBy("test1");

    when(groupRepository.save(group)).thenReturn(group);
    // When
    groupService.createGroup(group);
    // Then
    verify(groupRepository).save(group);
  }

  @Test
  void testUpdateGroup() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(new HashSet<>(Set.of("test1", "test2")));
    group.setCreatedBy("test1");

    when(groupRepository.findById("test1")).thenReturn(Optional.of(group));
    when(groupRepository.save(group)).thenReturn(group);
    // When
    groupService.updateGroup("test1", group);
    // Then
    verify(groupRepository).findById("test1");
    verify(groupRepository).save(group);
  }


}
