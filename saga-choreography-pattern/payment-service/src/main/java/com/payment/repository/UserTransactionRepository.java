package com.payment.repository;

import com.payment.entity.UserTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTransactionRepository extends MongoRepository<UserTransaction, String> {
}
