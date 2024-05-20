package com.hydra.divideup.repository;

import com.hydra.divideup.entity.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpenseRepository extends MongoRepository<Expense, String> {

}
