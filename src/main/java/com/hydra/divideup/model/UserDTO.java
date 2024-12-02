package com.hydra.divideup.model;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
  private String id;

  @NotNull(message = "phone number must not be null")
  private String phone;

  @NotNull(message = "email must not be null")
  private String email;

  @NotNull(message = "password must not be null")
  private String password;

  private String name;
  private String country;
  private String defaultCurrency;
  private String language;

  public UserDTO() {}

  public UserDTO(String phone, String email, String password) {
    this.phone = phone;
    this.email = email;
    this.password = password;
  }

  public UserDTO(
      String id,
      String phone,
      String email,
      String password,
      String name,
      String country,
      String defaultCurrency,
      String language) {
    this(phone, email, password);
    this.name = name;
    this.country = country;
    this.defaultCurrency = defaultCurrency;
    this.language = language;
  }
}
