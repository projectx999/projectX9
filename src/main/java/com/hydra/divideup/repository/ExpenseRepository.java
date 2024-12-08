package com.hydra.divideup.repository;

import com.hydra.divideup.entity.Expense;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpenseRepository extends MongoRepository<Expense, String> {
  List<Expense> findByGroupIdAndSettledTrue(String groupId);
}
