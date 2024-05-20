package com.hydra.divideup.entity;

import com.hydra.divideup.enums.SplitType;
import java.time.LocalDateTime;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Payment {

      private String id;
      private String userId;
      private String groupId;
      private String description;
      @NotNull
      private String paidBy;
      @NotNull
      private String currency;
      @NotNull
      private double amount;
      private LocalDateTime date;
      private String category;
      private String note;
      private boolean isSettled;
      @NotNull
      private SplitType splitType;
      private Map<String, Double> splitDetails;

}
