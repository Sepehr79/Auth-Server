package com.sepehr.authentication_server.model.entity;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("user")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class MongoUser extends User {
}
