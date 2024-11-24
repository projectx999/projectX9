package com.hydra.divideup.service;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.enums.SplitType;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.service.calculator.EqualExpenseCalculator;
import com.hydra.divideup.service.calculator.ExpenseCalculatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    //Mocks
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseCalculatorFactory expenseCalculatorFactory;
    @Mock private EqualExpenseCalculator equalExpenseCalculator;
    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void createExpenseTest(){
        //Given
        List<Expense> expenseList = new ArrayList<>();
        Payment payment = new Payment();
        payment.setSplitType(SplitType.EQUAL);


        when( expenseCalculatorFactory.getExpenseCalculator(payment.getSplitType())).thenReturn(equalExpenseCalculator);
        when( equalExpenseCalculator.calculateExpenses(payment)).thenReturn(expenseList);

        //when
        expenseService.createExpense(payment);

        //then
        verify(expenseCalculatorFactory,times(1)).getExpenseCalculator(payment.getSplitType());
        verify(equalExpenseCalculator,times(1)).calculateExpenses(payment);
        verify(expenseRepository,times(1)).saveAll(expenseList);



    }
}