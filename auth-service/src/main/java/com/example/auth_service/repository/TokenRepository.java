package com.example.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.example.auth_service.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long>{

}
