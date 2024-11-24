package com.hydra.divideup.entity;

import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.catalina.Manager;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Group {

  @Id private String id;
  private String groupName;
  private String groupDescription;
  @NotNull private Set<String> members;
  private String type;

  @CreatedBy @NotNull
  private String createdBy; // todo passing from UI will change to logged in user

  @CreatedDate private String createdTime;
  @LastModifiedBy private String updatedBy;
  @LastModifiedDate private String updatedTime;
  private Set<String> admin;

  public Group() {}

  public Group(String groupName, Set<String> members, String createdBy) {
    this.groupName = groupName;
    this.members = members;
    this.createdBy = createdBy;
  }
}
