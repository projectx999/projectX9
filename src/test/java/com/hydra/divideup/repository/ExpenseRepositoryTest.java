package com.hydra.divideup.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.hydra.divideup.entity.Expense;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

@DataMongoTest
@ActiveProfiles("test")
class ExpenseRepositoryTest {

  @Autowired private ExpenseRepository expenseRepository;

  @BeforeEach
  public void setUp() {
    expenseRepository.deleteAll();
  }

  @Test
  void testFindByGroupIdAndIsSettledTrue() {
    // Given
    String groupId = "testGroupId";
    Expense expense = new Expense();
    expense.setGroupId(groupId);
    expense.setSettled(true);
    expenseRepository.save(expense);

    // When
    List<Expense> expenses = expenseRepository.findByGroupIdAndSettledTrue(groupId);

    // Then
    assertFalse(expenses.isEmpty());
    assertEquals(1, expenses.size());
  }

  @Test
  void testFindByGroupIdAndIsSettledFalse() {
    // Given
    String groupId = "testGroupId";
    Expense expense = new Expense();
    expense.setGroupId(groupId);
    expense.setSettled(false);
    expenseRepository.save(expense);

    // When
    List<Expense> expenses = expenseRepository.findByGroupIdAndSettledTrue(groupId);

    // Then
    assertTrue(expenses.isEmpty());
  }
}
