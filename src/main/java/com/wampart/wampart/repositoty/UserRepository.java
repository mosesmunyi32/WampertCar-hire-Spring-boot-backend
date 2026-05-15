package com.wampart.wampart.repositoty;


import com.wampart.wampart.dto.response.UserResponse;
import com.wampart.wampart.enums.Role;
import com.wampart.wampart.model.UserEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByIdNumber(String IdNumber);
    List<UserEntity> findByRole(Role role);
    Boolean existsByEmail(String email);
    Boolean existsByIdNumber(String IdNumber);


}
