package com.wampert.wampert.repository;


import com.wampert.wampert.dto.response.UserResponse;
import com.wampert.wampert.enums.Role;
import com.wampert.wampert.model.UserEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByIdNumber(String IdNumber);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    List<UserEntity> findByRole(Role role);
    Boolean existsByEmail(String email);
    Boolean existsByIdNumber(String IdNumber);
    Optional<UserEntity> findByEmailOrIdNumber(String email, String IdNumber);


}
