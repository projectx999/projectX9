package com.hydra.divideup.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@Document
@Builder
@AllArgsConstructor
public class Expense {

  private String id;
  private String userId;
  private String groupId;
  private String paidBy;
  private String currency;

  @Field(targetType = FieldType.STRING)
  private BigDecimal amount;

  private LocalDateTime date;
  private String category;
  private String note;
  private boolean settled;
  private String paymentId;

  public Expense() {}

  public Expense(Payment payment, String userId, BigDecimal amount) {
    this.userId = userId;
    this.groupId = payment.getGroupId();
    this.paidBy = payment.getPaidBy();
    this.currency = payment.getCurrency();
    this.amount = amount;
    this.date = payment.getDate();
    this.category = payment.getCategory();
    this.note = payment.getNote();
    this.paymentId = payment.getId();
  }
}
