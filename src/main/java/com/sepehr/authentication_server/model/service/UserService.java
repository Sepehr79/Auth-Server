package com.sepehr.authentication_server.model.service;

import com.sepehr.authentication_server.bussiness.DataType;
import com.sepehr.authentication_server.bussiness.NumberGenerator;
import com.sepehr.authentication_server.controller.exception.UserNotFoundException;
import com.sepehr.authentication_server.controller.exception.WrongPasswordException;
import com.sepehr.authentication_server.controller.exception.WrongVerifierException;
import com.sepehr.authentication_server.model.entity.MongoUser;
import com.sepehr.authentication_server.model.entity.RedisUser;
import com.sepehr.authentication_server.model.entity.User;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.repo.MongoUserRepo;
import com.sepehr.authentication_server.model.repo.RedisUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final RedisUserRepo redisUserRepo;

    private final MongoUserRepo mongoUserRepo;

    private final NumberGenerator numberGenerator;

    private final PasswordEncoder passwordEncoder;
    
    public Pair<String, String> temporarySave(UserIO userIO){
        String verifierCode = numberGenerator.generateUserVerifierCode();

        RedisUser redisUser = new RedisUser();
        redisUser.setEmail(userIO.getEmail());
        redisUser.setPassword(passwordEncoder.encode(userIO.getPassword()));
        redisUser.setToken(verifierCode);
        redisUser.setRoles(userIO.getRole());
        redisUser.setAuthorities(userIO.getAuthority());

        redisUserRepo.save(redisUser);

        return Pair.of(userIO.getEmail(), verifierCode);
    }

    public User saveByEmailAndVerifier(String email, String verifier){
        var redisUserOptional = redisUserRepo.findById(email);
        if (redisUserOptional.isPresent()){
            if (redisUserOptional.get().getToken().equals(verifier)){
                RedisUser redisUser = redisUserOptional.get();
                return mongoUserRepo.save(
                        MongoUser.builder().email(redisUser.getEmail()).password(redisUser.getPassword())
                                .authorities(redisUser.getAuthorities()).roles(redisUser.getRoles()).build()
                );
            }
            throw new WrongVerifierException(email, verifier);
        }
        throw new UserNotFoundException(email, DataType.TEMPORARY);
    }

    public User verifyByEmailAndPassword(String email, String password){
        var mongoUserOptional = mongoUserRepo.findById(email);
        if (mongoUserOptional.isPresent()){
            if (passwordEncoder.matches(password, mongoUserOptional.get().getPassword())){
                return mongoUserOptional.get();
            }
            throw new WrongPasswordException(email, password);
        }
        throw new UserNotFoundException(email, DataType.PERMANENT);
    }

    public void changePasswordByEmailAndVerifier(String email, String verifier, String newPassword){
        var redisUser = redisUserRepo.findById(email)
                .orElseThrow(() -> new UserNotFoundException(email, DataType.TEMPORARY));
        var mongoUser = mongoUserRepo.findById(email)
                .orElseThrow(() -> new UserNotFoundException(email, DataType.PERMANENT));
        if (redisUser.getToken().equals(verifier)){
            mongoUserRepo.save(mongoUser.toBuilder().password(passwordEncoder.encode(newPassword)).build());
            return;
        }
        throw new WrongVerifierException(email, verifier);
    }

    public void deleteUserByEmail(String email){
        if (!mongoUserRepo.existsById(email))
            throw new UserNotFoundException(email, DataType.PERMANENT);
        mongoUserRepo.deleteById(email);
    }
    
}
