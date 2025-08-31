package com.github.luangust4vo.pw_leilao_backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.github.luangust4vo.pw_leilao_backend.models.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("FROM Person p WHERE p.email =:email")
    public Page<Person> findByEmail(@Param("email") String email, Pageable pageable);

    public Optional<Person> findByEmail(String email);

    Optional<Person> findByValidationCode(String validationCode);
}