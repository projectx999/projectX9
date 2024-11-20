package com.hydra.divideup.converter;

import com.hydra.divideup.entity.User;
import com.hydra.divideup.model.UserDTO;

public class UserConverter {

  // Convert User to UserDTO
  public static UserDTO convertToDTO(User user) {
    if (user == null) {
      return null;
    }

    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId());
    userDTO.setPhone(user.getPhone());
    userDTO.setEmail(user.getEmail());
    userDTO.setPassword(user.getPassword());
    userDTO.setName(user.getName());
    userDTO.setCountry(user.getCountry());
    userDTO.setDefaultCurrency(user.getDefaultCurrency());
    userDTO.setLanguage(user.getLanguage());

    return userDTO;
  }

  // Convert UserDTO to User
  public static User convertToEntity(UserDTO userDTO) {
    if (userDTO == null) {
      return null;
    }

    User user = new User();
    user.setId(userDTO.getId());
    user.setPhone(userDTO.getPhone());
    user.setEmail(userDTO.getEmail());
    user.setPassword(userDTO.getPassword());
    user.setName(userDTO.getName());
    user.setCountry(userDTO.getCountry());
    user.setDefaultCurrency(userDTO.getDefaultCurrency());
    user.setLanguage(userDTO.getLanguage());

    return user;
  }
}
