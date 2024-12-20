package com.hydra.divideup.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class User {

  @Id private String id;
  private String name;
  private String email;
  private String country;
  private String phone;

  @JsonIgnore private String password;
  private String defaultCurrency;
  private String language;
  private boolean isDeleted;
  private boolean isBlocked;

  public User() {}

  public User(String email, String phone, String password) {
    this.email = email;
    this.phone = phone;
    this.password = password;
  }
}
