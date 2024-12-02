package com.hydra.divideup.entity;

import com.hydra.divideup.enums.SplitType;
import java.time.LocalDateTime;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

  private String id;
  private String userId;
  private String groupId;
  private String description;
  @NotNull private String paidBy;
  @NotNull private String currency;
  @NotNull private double amount;
  private LocalDateTime date;
  private String category;
  private String note;
  private boolean settled;
  @NotNull private SplitType splitType;
  private Map<String, Double> splitDetails;

  public Payment(String paidBy, String currency, double amount, SplitType splitType) {
    this.paidBy = paidBy;
    this.category = category;
    this.splitType = splitType;
    this.currency = currency;
    this.amount = amount;
  }
}
