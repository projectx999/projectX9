package com.hydra.divideup.service;

import static com.hydra.divideup.exception.DivideUpError.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.entity.Payment;
import com.hydra.divideup.entity.User;
import com.hydra.divideup.enums.SplitType;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.repository.PaymentRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @InjectMocks
  private PaymentService paymentService;

  @Mock
  private ExpenseService expenseService;

  @Mock
  private PaymentRepository paymentRepository;
 
  @Mock
  private UserService userService;

  @Mock
  private GroupService groupService;

  private Payment payment;

  @BeforeEach
  void setUp() {
    payment = new Payment();
    payment.setGroupId("group");
    payment.setUserId("user");
    payment.setPaidBy("user");
    payment.setAmount(100.0);
    payment.setSplitType(SplitType.EQUAL);
    payment.setSplitDetails(Map.of("user1", 50.0, "user2", 50.0));
  }

  @Test
  void givenNullGroupId_whenCreatePayment_thenThrowIllegalOperationException() {
    // given
    payment.setGroupId(null);
    // when & then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(PAYMENT_VALIDATE_PAYEE.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }

  @Test
  void givenNullUserId_whenCreatePayment_thenThrowIllegalOperationException() {
    // given
    payment.setUserId(null);
    // when & then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(PAYMENT_VALIDATE_PAYEE.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }

  @Test
  void givenInvalidSplitType_whenCreatedPayment_thenThrowIllegalOperationException(){
    //given
    payment.setSplitType(null);
    //then
    IllegalOperationException exception =assertThrows(IllegalOperationException.class,()->paymentService.createPayment(payment));
    assertEquals(PAYMENT_SPLIT_TYPE.getMessage(),exception.getMessage());
    verify(paymentRepository,never()).save(payment);
  }

  @Test
  void givenInvalidPaidBy_whenCreatePayment_thenThrowIllegalOperationException() {
    // when
    when(userService.getUser("user")).thenThrow(new IllegalOperationException(USER_NOT_FOUND));
    // then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(USER_NOT_FOUND.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }

  @Test
  void givenNegativeAmount_whenCreatePayment_thenThrowIllegalOperationException() {
    // given
    payment.setAmount(-0.01);
    // when
    when(userService.getUser("user")).thenReturn(new User());
    // then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(PAYMENT_AMOUNT.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }

  @Test
  void givenZeroAmount_whenCreatePayment_thenThrowIllegalOperationException() {
    // given
    payment.setAmount(0.0);
    // when
    when(userService.getUser("user")).thenReturn(new User());
    // then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(PAYMENT_AMOUNT.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }

  @Test
  void givenInvalidSplitUsers_whenCreatePayment_thenThrowIllegalOperationException() {
    // when
    when(userService.getUser("user")).thenReturn(new User());
    when(userService.getUsers(payment.getSplitDetails().keySet())).thenReturn(List.of(new User()));
    // then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(PAYMENT_SPLIT_DETAILS.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }

  @Test
  void givenInvalidGroup_whenCreatePayment_thenThrowIllegalOperationException() {
    // when
    when(userService.getUser("user")).thenReturn(new User());
    when(userService.getUsers(payment.getSplitDetails().keySet())).thenReturn(List.of(new User()));
    when(groupService.getGroup(payment.getGroupId())).thenReturn(new Group());
    // then
    IllegalOperationException exception = assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));
    assertEquals(PAYMENT_SPLIT_DETAILS.getMessage(), exception.getMessage());
    verify(paymentRepository, never()).save(payment);
  }

  @Test
  void createPayment_shouldThrowException_whenSplitDetailsContainInvalidUsers() {
    // given
    User user1 = new User();
    user1.setId("user1");

    User userx = new User();
    userx.setId("userX");

    Payment payment = new Payment();
    payment.setId("payment1");
    payment.setGroupId("group1");
    payment.setUserId("user1");
    payment.setSplitType(SplitType.EQUAL);
    payment.setSplitDetails(Map.of(
            "user1", 50.0,
            "userX", 50.0 // "userX" is not in the group
    ));
    payment.setAmount(100.0);

    Group group = new Group();
    group.setId("group1");
    group.setMembers(Set.of("user1", "user2")); // Only "user1" and "user2" are valid group members
    //when
    when(userService.getUser("user1")).thenReturn(user1);
    when(userService.getUsers(any())).thenReturn(List.of(user1,userx));
    when(groupService.getGroup("group1")).thenReturn(group);

    //then
    IllegalOperationException exception= assertThrows(IllegalOperationException.class, () -> paymentService.createPayment(payment));

    assertEquals(PAYMENT_SPLIT_DETAILS.getMessage(), exception.getMessage());

    verify(paymentRepository, never()).save(payment);
    verify(expenseService, never()).createExpense(any());
  }



}
