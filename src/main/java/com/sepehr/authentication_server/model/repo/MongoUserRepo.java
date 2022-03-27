package com.sepehr.authentication_server.model.repo;

import com.sepehr.authentication_server.model.entity.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserRepo extends MongoRepository<MongoUser, String> {

}
