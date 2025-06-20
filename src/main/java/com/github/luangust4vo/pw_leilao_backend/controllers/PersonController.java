package com.github.luangust4vo.pw_leilao_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.services.PersonService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/people")
public class PersonController {
    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<Person>> findAll() {
        return ResponseEntity.ok(personService.findAll());
    }

    @PostMapping
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        return ResponseEntity.ok(personService.create(person));
    }

    @PutMapping
    public ResponseEntity<Person> update(@Valid @RequestBody Person person) {
        return ResponseEntity.ok(personService.update(person));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(Long id) {
        personService.delete(id);
        return ResponseEntity.ok("Person deleted successfully");
    }
}
