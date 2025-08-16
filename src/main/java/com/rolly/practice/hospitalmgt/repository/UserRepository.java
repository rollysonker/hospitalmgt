package com.rolly.practice.hospitalmgt.repository;


import com.rolly.practice.hospitalmgt.entity.User;
import com.rolly.practice.hospitalmgt.entity.type.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByProviderIdAndProviderType(String providerId, AuthProviderType providerType);
}