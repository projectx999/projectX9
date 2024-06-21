package com.hydra.divideup.controller;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.service.GroupService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("api/v1/groups")
public class GroupController {

  private final GroupService groupService;

  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<Group> getGroup(@PathVariable String id) {
    return ResponseEntity.ok(groupService.getGroup(id));
  }

  @GetMapping("user/{userId}")
  public ResponseEntity<List<Group>> getGroupsByUser(@PathVariable String userId) {
    return ResponseEntity.ok(groupService.getGroupsByUser(userId));
  }

  @PostMapping()
  public ResponseEntity<Group> createGroup(@RequestBody @Valid Group group) {
    return ResponseEntity.ok(groupService.createGroup(group));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Group> updateGroup(
      @PathVariable String id, @RequestBody @Valid Group group) {
    return ResponseEntity.ok(groupService.updateGroup(id, group));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Group> deleteGroup(@PathVariable String id) {
    return ResponseEntity.ok(groupService.deleteGroup(id));
  }
}
