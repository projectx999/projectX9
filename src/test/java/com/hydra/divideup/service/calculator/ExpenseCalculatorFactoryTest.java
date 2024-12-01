package com.hydra.divideup.service.calculator;

import com.hydra.divideup.enums.SplitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExpenseCalculatorFactoryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private EqualExpenseCalculator equalExpenseCalculator;

    @Mock
    private UnEqualExpenseCalculator unEqualExpenseCalculator;

    @Mock
    private PercentageExpenseCalculator percentageExpenseCalculator;

    @Mock
    private ShareExpenseCalculator shareExpenseCalculator;

    @Mock
    private FullExpenseCalculator fullExpenseCalculator;

    @InjectMocks
    private ExpenseCalculatorFactory expenseCalculatorFactory;

    @BeforeEach
    void setup() {
        when(applicationContext.getBean("equalExpenseCalculator")).thenReturn(equalExpenseCalculator);
        when(applicationContext.getBean("unEqualExpenseCalculator")).thenReturn(unEqualExpenseCalculator);
        when(applicationContext.getBean("percentageExpenseCalculator")).thenReturn(percentageExpenseCalculator);
        when(applicationContext.getBean("shareExpenseCalculator")).thenReturn(shareExpenseCalculator);
        when(applicationContext.getBean("fullExpenseCalculator")).thenReturn(fullExpenseCalculator);
    }

    @Test
    void testGetExpenseCalculatorEqual() {
        ExpenseCalculator calculator = expenseCalculatorFactory.getExpenseCalculator(SplitType.EQUAL);
        assertEquals(equalExpenseCalculator, calculator, "Expected EqualExpenseCalculator instance");
        assertInstanceOf(EqualExpenseCalculator.class, calculator, "Expected calculator to be of type EqualExpenseCalculator");
        verify(applicationContext, times(1)).getBean("equalExpenseCalculator");
    }

    @Test
    void testGetExpenseCalculatorUnequal() {
        ExpenseCalculator calculator = expenseCalculatorFactory.getExpenseCalculator(SplitType.UNEQUAL);
        assertEquals(unEqualExpenseCalculator, calculator, "Expected UnEqualExpenseCalculator instance");
        assertInstanceOf(UnEqualExpenseCalculator.class, calculator, "Expected calculator to be of type UnEqualExpenseCalculator");
        verify(applicationContext, times(1)).getBean("unEqualExpenseCalculator");
    }

    @Test
    void testGetExpenseCalculatorPercentage() {
        ExpenseCalculator calculator = expenseCalculatorFactory.getExpenseCalculator(SplitType.PERCENTAGE);
        assertEquals(percentageExpenseCalculator, calculator, "Expected PercentageExpenseCalculator instance");
        assertInstanceOf(PercentageExpenseCalculator.class, calculator, "Expected calculator to be of type PercentageExpenseCalculator");
        verify(applicationContext, times(1)).getBean("percentageExpenseCalculator");
    }

    @Test
    void testGetExpenseCalculatorShare() {
        ExpenseCalculator calculator = expenseCalculatorFactory.getExpenseCalculator(SplitType.SHARE);
        assertEquals(shareExpenseCalculator, calculator, "Expected ShareExpenseCalculator instance");
        assertInstanceOf(ShareExpenseCalculator.class, calculator, "Expected calculator to be of type ShareExpenseCalculator");
        verify(applicationContext, times(1)).getBean("shareExpenseCalculator");
    }

    @Test
    void testGetExpenseCalculatorFull() {
        ExpenseCalculator calculator = expenseCalculatorFactory.getExpenseCalculator(SplitType.FULL);
        assertEquals(fullExpenseCalculator, calculator, "Expected FullExpenseCalculator instance");
        assertInstanceOf(FullExpenseCalculator.class, calculator, "Expected calculator to be of type FullExpenseCalculator");
        verify(applicationContext, times(1)).getBean("fullExpenseCalculator");
    }


}
