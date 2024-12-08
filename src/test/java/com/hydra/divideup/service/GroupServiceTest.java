package com.hydra.divideup.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.repository.GroupRepository;
import com.hydra.divideup.repository.PaymentRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    group.setMembers(new HashSet<>(Set.of("testM1", "testM2")));
    group.setCreatedBy("testUser");

    when(groupRepository.findById("test1")).thenReturn(Optional.of(group));
    when(groupRepository.save(group)).thenReturn(group);
    // When
    groupService.updateGroup("test1", group);
    // Then
    verify(groupRepository).findById("test1");
    verify(groupRepository).save(group);
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
    group.setSettled(true);

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
