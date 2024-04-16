package com.example.easyloan.repository;

import com.example.easyloan.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query(value = "SELECT * FROM token WHERE users_id = ?1 AND (is_expired = 'false' or is_revoked = 'false')", nativeQuery = true)
    List<Token> findAllValidTokenByUser(Long id);

    Optional<Token> findByToken(String token);
}
