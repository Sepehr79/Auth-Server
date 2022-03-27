package com.sepehr.authentication_server.model.service;

import com.sepehr.authentication_server.bussiness.NumberGenerator;
import com.sepehr.authentication_server.controller.exception.UserNotFoundException;
import com.sepehr.authentication_server.controller.exception.WrongVerifierException;
import com.sepehr.authentication_server.model.entity.MongoUser;
import com.sepehr.authentication_server.model.entity.RedisUser;
import com.sepehr.authentication_server.model.io.UserIO;
import com.sepehr.authentication_server.model.repo.MongoUserRepo;
import com.sepehr.authentication_server.model.repo.RedisUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final RedisUserRepo redisUserRepo;

    private final MongoUserRepo mongoUserRepo;

    private final NumberGenerator numberGenerator;
    
    public Pair<String, String> temporarySave(UserIO userIO){
        String verifierCode = numberGenerator.generateUserVerifierCode();

        RedisUser redisUser = new RedisUser();
        redisUser.setEmail(userIO.getEmail());
        redisUser.setPassword(userIO.getPassword());
        redisUser.setToken(verifierCode);
        redisUser.setRoles(userIO.getRole());
        redisUser.setAuthorities(userIO.getAuthority());

        redisUserRepo.save(redisUser);

        return Pair.of(userIO.getEmail(), verifierCode);
    }

    public void saveByEmailAndVerifier(String email, String verifier){
        var redisUserOptional = redisUserRepo.findById(email);
        if (redisUserOptional.isPresent()){
            if (redisUserOptional.get().getToken().equals(verifier)){
                RedisUser redisUser = redisUserOptional.get();
                mongoUserRepo.save(
                        MongoUser.builder().email(redisUser.getEmail()).password(redisUser.getPassword())
                                .authorities(redisUser.getAuthorities()).roles(redisUser.getRoles()).build()
                );
                return;
            }
            throw new WrongVerifierException(email, verifier);
        }
        throw new UserNotFoundException(email);
    }
    
}
