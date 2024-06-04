package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.GROUP_NOT_FOUND;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.DivideUpError;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.repository.GroupRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  private final GroupRepository groupRepository;

  public GroupService(GroupRepository groupRepository) {
    this.groupRepository = groupRepository;
  }

  public List<Group> getGroupsByUser(String userId) {
    return groupRepository.findByMembersContains(userId);
  }

  public Group getGroup(String id) {
    return groupRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(
        GROUP_NOT_FOUND));
  }

  public Group createGroup(Group group) {
    group.getMembers().add(group.getCreatedBy());
    return groupRepository.save(group);
  }

  public Group updateGroup(String groupId, Group group) {
    Group existingGroup = groupRepository.findById(groupId)
        .orElseThrow(() -> new RecordNotFoundException(GROUP_NOT_FOUND));
    group.setId(groupId);
    return groupRepository.save(existingGroup);
  }

  public Group deleteGroup(String id) {
    Group group = groupRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException(GROUP_NOT_FOUND));
    if (!group.isSettled()) {
      throw new IllegalOperationException(DivideUpError.GROUP_DELETE_UNSETTLE);
    }
    groupRepository.deleteById(id);
    return group;
  }
}
