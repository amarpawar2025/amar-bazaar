package com.amar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amar.modal.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}