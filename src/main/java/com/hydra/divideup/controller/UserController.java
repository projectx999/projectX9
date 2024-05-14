package com.hydra.divideup.controller;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.model.UserDTO;
import com.hydra.divideup.service.UserService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("api/v1/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }

  @PostMapping()
  public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO user) {
    return ResponseEntity.ok(userService.createUser(user));
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.getUser(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody @Valid User user) {
    return ResponseEntity.ok(userService.updateUser(id, user));
  }

  @PutMapping("/{id}/block")
  public ResponseEntity<User> blockUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.blockUser(id));
  }

  @PutMapping("/{id}/unblock")
  public ResponseEntity<User> unblockUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.unblockUser(id));
  }

  @PutMapping("/{id}/delete")
  public ResponseEntity<User> deleteUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.deleteUser(id));
  }

}
