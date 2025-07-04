package com.example.daoGenerator.dao.interfaces;

import java.util.List;
import java.util.Optional;
import com.example.pojogenerator.pojos.ClientCredential;

/**
 * Enhanced DAO interface for ClientCredential entity
 * Table: client_credentials
 * Key Type: CUSTOM_SINGLE_KEY
 * Generated by Enhanced JDBC DAO Generator
 */
public interface ClientCredentialDao {

    // Custom single key operations
    Optional<ClientCredential> findById(String id);
    void deleteById(String id);
    boolean existsById(String id);

    // Common operations
    List<ClientCredential> findAll();
    ClientCredential save(ClientCredential entity);
    long count();

}
