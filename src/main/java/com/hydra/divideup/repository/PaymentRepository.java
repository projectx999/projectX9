package com.hydra.divideup.repository;

import com.hydra.divideup.entity.Payment;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

  List<Payment> findByGroupIdAndSettledTrue(String groupId);
}
