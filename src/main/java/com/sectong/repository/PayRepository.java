package com.sectong.repository;

import com.sectong.domain.PayMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * PayMessage类的CRUD操作
 */
public interface PayRepository extends MongoRepository<PayMessage, Long> {

    List<PayMessage> findByOdoouserid(String userid);
}