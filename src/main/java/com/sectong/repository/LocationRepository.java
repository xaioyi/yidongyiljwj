package com.sectong.repository;

import com.sectong.domain.UserLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * UserLocation类的CRUD操作
 */
public interface LocationRepository extends MongoRepository<UserLocation, Long> {

    List<UserLocation> findByTelephone(String telephone);

}