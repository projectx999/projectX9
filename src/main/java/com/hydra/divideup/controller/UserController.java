package com.hydra.divideup.controller;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.model.UserDTO;
import com.hydra.divideup.service.UserService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody @Valid UserDTO userDTO) {
    return ResponseEntity.ok(userService.updateUser(id, userDTO));
  }

  @PutMapping("/block/{id}")
  public ResponseEntity<User> blockUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.blockUser(id));
  }

  @PutMapping("/unblock/{id}")
  public ResponseEntity<User> unblockUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.unblockUser(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<User> deleteUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.deleteUser(id));
  }

}
