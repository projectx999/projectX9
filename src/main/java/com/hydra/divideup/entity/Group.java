package com.hydra.divideup.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Group {

  @Id
  private String id;
  private String groupName;
  private String groupDescription;
  private List<String> members;
  private String type;
}
