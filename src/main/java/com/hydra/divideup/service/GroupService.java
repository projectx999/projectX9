package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.GROUP_NOT_FOUND;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.DivideUpError;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.repository.GroupRepository;
import com.hydra.divideup.repository.PaymentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private final GroupRepository groupRepository;

  private final ExpenseRepository expenseRepository;

  private final PaymentRepository paymentRepository;

  private final Supplier<RecordNotFoundException> groupNotFoundSupplier =
      () -> new RecordNotFoundException(GROUP_NOT_FOUND);

  public GroupService(
      GroupRepository groupRepository,
      ExpenseRepository expenseRepository,
      PaymentRepository paymentRepository) {
    this.groupRepository = groupRepository;
    this.expenseRepository = expenseRepository;
    this.paymentRepository = paymentRepository;
  }

  public List<Group> getGroupsByUser(String userId) {
    return groupRepository.findByMembersContains(userId);
  }

  public Group getGroup(String id) {
    return groupRepository.findById(id).orElseThrow(groupNotFoundSupplier);
  }

  public Group createGroup(Group group) {
    // Ensure at least 2 members (including the creator)
    if (group.getMembers() == null || group.getMembers().size() < 2) {
      throw new IllegalArgumentException("Cannot create a group with less than 2 members.");
    }

    // Adding created user as member
    group.getMembers().add(group.getCreatedBy());
    group.setAdmin(Set.of(group.getCreatedBy()));

    return groupRepository.save(group);
  }

  //TODO CHeck current user is an admin(spring security)
  public Group updateGroup(String groupId, Group group) {
    Group existingGroup = groupRepository.findById(groupId).orElseThrow(groupNotFoundSupplier);

    // Validate and update members
    if (group.getMembers() == null || group.getMembers().size() < 2) {
      throw new IllegalArgumentException("Members list cannot be null or empty.");
    }
    existingGroup.setMembers(group.getMembers());

    // Validate and update admins
    if (group.getAdmin() == null) {
      throw new IllegalArgumentException("Admins list cannot be null.");
    }

    Set<String> validAdmins = new HashSet<>();
    for (String admin : group.getAdmin()) {
      if (existingGroup.getMembers().contains(admin)) {
        validAdmins.add(admin); // Add only admins who are still members
      } else {
        System.out.println("Admin " + admin + " is not a member and will not be retained.");
      }
    }

    // Ensure there's at least one admin in the updated list
    if (validAdmins.isEmpty()) {
      throw new IllegalArgumentException("Updated group must have at least one admin.");
    }

    existingGroup.setAdmin(validAdmins);

    // Save and return the updated group
    return groupRepository.save(existingGroup);
  }

  public Group deleteGroup(String id) {
    //TODO
    Group group = groupRepository.findById(id).orElseThrow(groupNotFoundSupplier);
    if (haveNoPendingNonSettledExpense(id)) {
      throw new IllegalOperationException(DivideUpError.GROUP_DELETE_UNSETTLE);
    }
    groupRepository.deleteById(id);
    return group;
  }

  private boolean haveNoPendingNonSettledExpense(String groupId) {
    var expenses = expenseRepository.findByGroupIdAndSettledTrue(groupId);
    var payments = paymentRepository.findByGroupIdAndSettledTrue(groupId);
    return !(expenses.isEmpty() && payments.isEmpty());
  }
}
