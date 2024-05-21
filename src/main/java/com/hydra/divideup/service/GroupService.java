package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.GROUP_NOT_FOUND;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.DivideUpError;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.repository.GroupRepository;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private final GroupRepository groupRepository;

  private final Supplier<RecordNotFoundException> groupNotFoundSupplier =
      () -> new RecordNotFoundException(DivideUpError.GROUP_NOT_FOUND);

  public GroupService(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  public List<Group> getGroupsByUser(String userId) {
    return groupRepository.findByMembersContains(userId);
  }

  public Group getGroup(String id) {
    return groupRepository.findById(id)
        .orElseThrow(groupNotFoundSupplier);
  }

  public Group createGroup(Group group) {
    group.getMembers().add(group.getCreatedBy());
    return groupRepository.save(group);
  }

  public Group updateGroup(String groupId, Group group) {
    Group existingGroup = groupRepository.findById(groupId)
        .orElseThrow(groupNotFoundSupplier);
    group.setId(groupId);
    return groupRepository.save(existingGroup);
  }

  //todo validations of delete group
  public Group deleteGroup(String id) {
    Group group = groupRepository.findById(id)
        .orElseThrow(groupNotFoundSupplier);
    if (!group.isSettled()) {
      throw new IllegalOperationException(DivideUpError.GROUP_DELETE_UNSETTLE);
    }
    groupRepository.deleteById(id);
    return group;
  }
}
