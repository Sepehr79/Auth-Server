package com.sepehr.authentication_server.model.repo;

import com.sepehr.authentication_server.model.entity.RedisUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepo extends CrudRepository<RedisUser, String> {



}
