package com.hydra.divideup.model;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
  private String id;
  private String phoneNumber;
  private String email;
  private String password;
  private String name;
  private String country;
  private String defaultCurrency;
  private String language;

  public UserDTO() {}

  public UserDTO(
      @NotNull(message = "phone number must not be null") String phoneNumber,
      @NotNull(message = "email must not be null") String email,
      @NotNull(message = "password must not be null") String password) {
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.password = password;
  }

  public UserDTO(
      @NotNull(message = "phone number must not be null") String phoneNumber,
      @NotNull(message = "email must not be null") String email,
      @NotNull(message = "password must not be null") String password,
      String id,
      String name,
      String country,
      String defaultCurrency,
      String language) {
    this(phoneNumber, email, password);
    this.name = name;
    this.country = country;
    this.defaultCurrency = defaultCurrency;
    this.language = language;
  }
}
