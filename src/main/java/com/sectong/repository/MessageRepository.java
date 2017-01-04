package com.sectong.repository;

import com.sectong.domain.UserMessages;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * UserMessage类的CRUD操作
 */
public interface MessageRepository extends MongoRepository<UserMessages, Long> {

    List<UserMessages> findByTargetid(String mytargetid);

}