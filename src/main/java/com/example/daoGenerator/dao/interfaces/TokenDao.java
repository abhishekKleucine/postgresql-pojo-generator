package com.example.daoGenerator.dao.interfaces;

import java.util.List;
import java.util.Optional;
import com.example.pojogenerator.pojos.Token;

/**
 * Enhanced DAO interface for Token entity
 * Table: tokens
 * Key Type: CUSTOM_SINGLE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public interface TokenDao {

    // Custom single key operations
    Optional<Token> findByToken(String token);
    void deleteByToken(String token);
    boolean existsByToken(String token);

    // Common operations
    List<Token> findAll();
    Token save(Token entity);
    long count();

}
