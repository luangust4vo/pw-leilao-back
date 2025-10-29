package com.github.luangust4vo.pw_leilao_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.services.PersonService;
import com.github.luangust4vo.pw_leilao_backend.dto.ApiResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.ChangePasswordRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.UpdatePersonRequestDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/people")
public class PersonController {
    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<?> findAll(Pageable pageable) {
        return ResponseEntity.ok(personService.findAllPeople(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(personService.findPersonById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Person person) {
        return ResponseEntity.ok(personService.create(person));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody UpdatePersonRequestDTO updatePersonRequestDTO) {
        return ResponseEntity.ok(personService.update(id, updatePersonRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<?>> delete(@PathVariable("id") Long id) {
        personService.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Pessoa excluída com sucesso."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal Person loggedInUser) {
        return ResponseEntity.ok(personService.findPersonById(loggedInUser.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyInfo(@AuthenticationPrincipal Person loggedInUser, @RequestBody UpdatePersonRequestDTO dto) {
        return ResponseEntity.ok(personService.update(loggedInUser.getId(), dto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponseDTO<?>> deleteMyAccount(@AuthenticationPrincipal Person loggedInUser) {
        personService.changeStatus(loggedInUser.getId(), false);
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Sua conta foi excluída com sucesso."));
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<?> changeMyPassword(@AuthenticationPrincipal Person loggedInUser, @Valid @RequestBody ChangePasswordRequestDTO dto) {
        personService.changePassword(loggedInUser, dto);
        return ResponseEntity.ok(ApiResponseDTO.sucesso("Senha alterada com sucesso."));
    }
}
