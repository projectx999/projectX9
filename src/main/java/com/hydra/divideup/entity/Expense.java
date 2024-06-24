package com.hydra.divideup.entity;

import com.hydra.divideup.enums.Currency;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Expense {

  private String id;
  private String userId;
  private String groupId;
  private String paidBy;
  private Currency currency;
  private double amount;
  private LocalDateTime date;
  private String category;
  private String note;
  private boolean settled;
  private String paymentId;
}
