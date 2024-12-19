package com.hydra.divideup.service.expensemanager;

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
public class ExpenseManagerFactoryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private EqualExpenseManager equalExpenseManager;

    @Mock
    private UnEqualExpenseManager unEqualExpenseManager;

    @Mock
    private PercentageExpenseManager percentageExpenseManager;

    @Mock
    private ShareExpenseManager shareExpenseManager;
    

    @InjectMocks
    private ExpenseManagerFactory expenseManagerFactory;

    @BeforeEach
    void setup() {
        when(applicationContext.getBean("equalExpenseManager")).thenReturn(equalExpenseManager);
        when(applicationContext.getBean("unEqualExpenseManager")).thenReturn(unEqualExpenseManager);
        when(applicationContext.getBean("percentageExpenseManager")).thenReturn(percentageExpenseManager);
        when(applicationContext.getBean("shareExpenseManager")).thenReturn(shareExpenseManager);
    }


    @Test
    void testGetExpenseManagerEqual() {
        ExpenseManager manager = expenseManagerFactory.getExpenseManager(SplitType.EQUAL);
        assertEquals(equalExpenseManager, manager, "Expected equalExpenseManager instance");
        assertInstanceOf(EqualExpenseManager.class, manager, "Expected manager to be of type equalExpenseManager");
        verify(applicationContext, times(1)).getBean("equalExpenseManager");
    }

    @Test
    void testGetExpenseManagerUnequal() {
        ExpenseManager manager = expenseManagerFactory.getExpenseManager(SplitType.UNEQUAL);
        assertEquals(unEqualExpenseManager, manager, "Expected UnequalExpenseManager instance");
        assertInstanceOf(UnEqualExpenseManager.class, manager, "Expected manager to be of type UnequalExpenseManager");
        verify(applicationContext, times(1)).getBean("unEqualExpenseManager");
    }

    @Test
    void testGetExpenseManagerPercentage() {
        ExpenseManager manager = expenseManagerFactory.getExpenseManager(SplitType.PERCENTAGE);
        assertEquals(percentageExpenseManager, manager, "Expected percentageExpenseManager instance");
        assertInstanceOf(PercentageExpenseManager.class, manager, "Expected manager to be of type percentageExpenseManager");
        verify(applicationContext, times(1)).getBean("percentageExpenseManager");
    }

    @Test
    void testGetExpenseManagerShare() {
        ExpenseManager manager = expenseManagerFactory.getExpenseManager(SplitType.SHARE);
        assertEquals(shareExpenseManager, manager, "Expected shareExpenseManager instance");
        assertInstanceOf(ShareExpenseManager.class, manager, "Expected manager to be of type shareExpenseManager");
        verify(applicationContext, times(1)).getBean("shareExpenseManager");
    }



}
