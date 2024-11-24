package com.hydra.divideup.service;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.repository.GroupRepository;
import com.hydra.divideup.repository.PaymentRepository;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

  @Mock private GroupRepository groupRepository;

  @Mock private ExpenseRepository expenseRepository;

  @Mock private PaymentRepository paymentRepository;

  @InjectMocks private GroupService groupService;

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

    // Mock behavior for saving the group
    when(groupRepository.save(any(Group.class))).thenReturn(group);

    // When
    Group createdGroup = groupService.createGroup(group);

    // Then
    verify(groupRepository, times(1)).save(group); // Verify save is called exactly once
    assertNotNull(createdGroup);
    assertEquals("Test Group", createdGroup.getGroupName());
    assertTrue(createdGroup.getMembers().contains("test1"));
    assertTrue(createdGroup.getMembers().contains("test2"));
    assertTrue(createdGroup.getAdmin().contains("test1")); // Created user should be an admin
  }

  @Test
  public void testCreateGroupWithNullMembers() {
    // Arrange
    Group newGroup = new Group();
    newGroup.setGroupName("Test Group");
    newGroup.setMembers(null);
    newGroup.setCreatedBy("test1");

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      groupService.createGroup(newGroup);
    });

    assertNotNull(exception.getMessage());
    verify(groupRepository, never()).save(any(Group.class));
  }

  @Test
  public void testCreateGroupWithLessThanTwoMembers() {
    // Arrange
    Group newGroup = new Group();
    newGroup.setGroupName("creator");
    newGroup.setMembers(Set.of("user1"));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      groupService.createGroup(newGroup);
    });

    assertEquals("Cannot create a group with less than 2 members.", exception.getMessage());
    verify(groupRepository, never()).save(any(Group.class));
  }

  @Test
  void testUpdateGroup() {
    // Given
    Group originalGroup = new Group();
    originalGroup.setId("test1");
    originalGroup.setGroupName("Test Group");
    originalGroup.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    originalGroup.setAdmin(Set.of("testM1"));

    // Define updated members and admins
    Group updatedGroup = new Group();
    updatedGroup.setId("test1");
    updatedGroup.setGroupName("Updated Test Group");
    updatedGroup.setMembers(new HashSet<>(Set.of("testM1", "testM3"))); // Updated members
    updatedGroup.setAdmin(Set.of("testM3")); // Updated admins to only include "testM3"

    // Mock behavior for finding and saving the group
    when(groupRepository.findById("test1")).thenReturn(Optional.of(originalGroup));
    when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);

    // When
    Group resultGroup = groupService.updateGroup("test1", updatedGroup);

    // Then
    verify(groupRepository).findById("test1");
    verify(groupRepository).save(originalGroup); // Verify save was called on the existing group

    assertNotNull(resultGroup);
    assertEquals("Updated Test Group", resultGroup.getGroupName());
    assertEquals(Set.of("testM1", "testM3"), resultGroup.getMembers()); // Check updated members
    assertEquals(Set.of("testM3"), resultGroup.getAdmin()); // Check updated admins
  }

  @Test
  public void testUpdateGroupWithNullMembers() {
    // Arrange
    Group originalGroup = new Group();
    originalGroup.setId("test1");
    originalGroup.setGroupName("Test Group");
    originalGroup.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    originalGroup.setAdmin(Set.of("testM1"));

    // Define updated members and admins
    Group updatedGroup = new Group();
    updatedGroup.setId("test1");
    updatedGroup.setGroupName("Updated Test Group");
    updatedGroup.setMembers(null); // Invalid members list

    when(groupRepository.findById("test1")).thenReturn(Optional.of(originalGroup));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      groupService.updateGroup("test1", updatedGroup);
    });

    // Verify
    assertEquals("Members list cannot be null or empty.", exception.getMessage());
    verify(groupRepository, never()).save(any(Group.class)); // Ensure no save operation
  }

  @Test
  public void testUpdateGroupWithLessThanTwoMembers() {
    // Arrange
    Group originalGroup = new Group();
    originalGroup.setId("test1");
    originalGroup.setGroupName("Test Group");
    originalGroup.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    originalGroup.setAdmin(Set.of("testM1"));

    // Define updated members and admins
    Group updatedGroup = new Group();
    updatedGroup.setId("test1");
    updatedGroup.setGroupName("Updated Test Group");
    updatedGroup.setMembers(Set.of("testM1")); // Invalid members list
    when(groupRepository.findById("test1")).thenReturn(Optional.of(originalGroup));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      groupService.updateGroup("test1", updatedGroup);
    });

    assertEquals("Members list cannot be null or empty.", exception.getMessage());
    verify(groupRepository, never()).save(any(Group.class));
  }

  @Test
  public void testUpdateGroupWithInvalidAdmins() {
    // Arrange
    Group originalGroup = new Group();
    originalGroup.setId("test1");
    originalGroup.setGroupName("Test Group");
    originalGroup.setMembers(new HashSet<>(Set.of("testM1", "testM2", "testM3")));
    originalGroup.setAdmin(Set.of("testM1"));

    // Define updated members and admins
    Group updatedGroup = new Group();
    updatedGroup.setId("test1");
    updatedGroup.setGroupName("Updated Test Group");
    updatedGroup.setMembers(Set.of("testM1", "testM2", "testM3"));
    originalGroup.setAdmin(null);

    when(groupRepository.findById("test1")).thenReturn(Optional.of(originalGroup));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      groupService.updateGroup("test1", updatedGroup);
    });

    assertEquals("Admins list cannot be null.", exception.getMessage());
    verify(groupRepository, never()).save(any(Group.class));
  }

  @Test
  public void testUpdateGroupRetainsValidAdmins() {
    // Arrange
    Group originalGroup = new Group();
    originalGroup.setId("test1");
    originalGroup.setGroupName("Test Group");
    originalGroup.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    originalGroup.setAdmin(Set.of("testM1")); // Initial admin is "testM1"

    // Define updated members and admins
    Group updatedGroup = new Group();
    updatedGroup.setId("test1");
    updatedGroup.setGroupName("Updated Test Group");
    updatedGroup.setMembers(Set.of("testM1", "testM2")); // No change in members
    updatedGroup.setAdmin(Set.of("testM2")); // Update admin to "testM2"

    // Mock group repository behavior
    when(groupRepository.findById("test1")).thenReturn(Optional.of(originalGroup));
    when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return saved group

    // Act
    Group resultGroup = groupService.updateGroup("test1", updatedGroup);

    // Assert
    assertNotNull(resultGroup);
    assertEquals(Set.of("testM1", "testM2"), resultGroup.getMembers()); // Ensure members remain unchanged
    assertEquals(Set.of("testM2"), resultGroup.getAdmin()); // Ensure admin is updated to "testM2"

    // Verify repository interactions
    verify(groupRepository, times(1)).findById("test1");
    verify(groupRepository, times(1)).save(any(Group.class));
  }

  @Test
  public void testUpdateGroupWithNonMemberAdmin() {
    // Arrange
    Group originalGroup = new Group();
    originalGroup.setId("test1");
    originalGroup.setGroupName("Test Group");
    originalGroup.setMembers(new HashSet<>(Set.of("testM1", "testM2"))); // Members: testM1, testM2
    originalGroup.setAdmin(Set.of("testM1")); // Admin: testM1

    Group updatedGroup = new Group();
    updatedGroup.setId("test1");
    updatedGroup.setGroupName("Updated Test Group");
    updatedGroup.setMembers(Set.of("testM1", "testM2")); // Members remain unchanged
    updatedGroup.setAdmin(Set.of("testM3")); // Attempting to add non-member "testM3" as admin

    // Mock group repository behavior
    when(groupRepository.findById("test1")).thenReturn(Optional.of(originalGroup));

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      groupService.updateGroup("test1", updatedGroup);
    });

    // Verify
    assertEquals("Updated group must have at least one admin.", exception.getMessage());
    verify(groupRepository, times(1)).findById("test1");
    verify(groupRepository, never()).save(any(Group.class)); // Ensure no save operation
  }

  @Test
  void testNoGroupFoundUpdateGroup() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    group.setCreatedBy("testUser");

    when(groupRepository.findById("test1")).thenReturn(Optional.empty());
    // When
    assertThrows(RecordNotFoundException.class, () -> groupService.updateGroup("test1", group));
    // Then
    verify(groupRepository).findById("test1");
  }

  @Test
  void testDeleteGroup() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    group.setCreatedBy("testUser");
    //group.setSettled(true);

    // Given
    String groupId = "test1";
    when(expenseRepository.findByGroupIdAndSettledTrue(groupId)).thenReturn(List.of());

    when(paymentRepository.findByGroupIdAndSettledTrue(groupId)).thenReturn(List.of());

    when(groupRepository.findById("test1")).thenReturn(Optional.of(group));
    // When
    groupService.deleteGroup("test1");
    // Then
    verify(groupRepository).findById("test1");
    verify(groupRepository).deleteById("test1");
    verify(expenseRepository).findByGroupIdAndSettledTrue("test1");
    verify(paymentRepository).findByGroupIdAndSettledTrue("test1");
  }

  @Test
  void testDeleteGroupWithUnsettledExpense() {
    // Given
    Group group = new Group();
    group.setGroupName("Test Group");
    group.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    group.setCreatedBy("testUser");

    // Given
    String groupId = "test1";
    Expense expense = new Expense();
    expense.setGroupId(groupId);
    expense.setSettled(false);
    when(expenseRepository.findByGroupIdAndSettledTrue(groupId)).thenReturn(List.of(expense));

    when(paymentRepository.findByGroupIdAndSettledTrue(groupId)).thenReturn(List.of());

    when(groupRepository.findById("test1")).thenReturn(Optional.of(group));
    // When
    assertThrows(IllegalOperationException.class, () -> groupService.deleteGroup("test1"));
    // Then
    verify(groupRepository).findById("test1");
    verify(expenseRepository).findByGroupIdAndSettledTrue("test1");
    verify(paymentRepository).findByGroupIdAndSettledTrue("test1");
  }
}
