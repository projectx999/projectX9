package com.hydra.divideup.service;

import com.hydra.divideup.entity.Expense;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.enums.SplitType;
import com.hydra.divideup.repository.ExpenseRepository;
import com.hydra.divideup.service.calculator.EqualExpenseCalculator;
import com.hydra.divideup.service.calculator.ExpenseCalculator;
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
        when(expenseRepository.saveAll(expenseList)).thenReturn(expenseList);

        //when
        expenseService.createExpense(payment);

        //then
        verify(expenseCalculatorFactory).getExpenseCalculator(payment.getSplitType());
        verify(equalExpenseCalculator).calculateExpenses(payment);
        verify(expenseRepository).saveAll(expenseList);



    }
}
