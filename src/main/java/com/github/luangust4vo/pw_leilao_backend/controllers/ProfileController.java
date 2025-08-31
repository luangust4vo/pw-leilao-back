package com.github.luangust4vo.pw_leilao_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.luangust4vo.pw_leilao_backend.models.Profile;
import com.github.luangust4vo.pw_leilao_backend.services.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @GetMapping
    public Page<Profile> findAll(Pageable page) {
        return profileService.findAll(page);
    }

    @GetMapping("/{id}")
    public Profile findById(@PathVariable("id") long id) {
        return profileService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profile create(@Valid @RequestBody Profile profile) {
        return profileService.create(profile);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Profile update(@PathVariable("id") long id, @Valid @RequestBody Profile newProfile) {
        Profile profile = profileService.findById(id);
        profile.setType(newProfile.getType());
        
        return profileService.update(profile);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        profileService.delete(id);
    }

}