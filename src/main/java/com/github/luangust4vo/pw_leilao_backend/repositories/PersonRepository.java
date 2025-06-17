package com.github.luangust4vo.pw_leilao_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.luangust4vo.pw_leilao_backend.models.Person;

public interface PersonRepository extends JpaRepository<Person, Long>{
    
}