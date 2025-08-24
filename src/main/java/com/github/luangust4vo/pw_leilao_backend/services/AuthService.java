package com.github.luangust4vo.pw_leilao_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.luangust4vo.pw_leilao_backend.dto.AuthResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.PersonRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.PersonProfile;
import com.github.luangust4vo.pw_leilao_backend.models.Profile;
import com.github.luangust4vo.pw_leilao_backend.models.enums.ProfileType;
import com.github.luangust4vo.pw_leilao_backend.repositories.ProfileRepository;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PersonService personService;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponseDTO auth(PersonRequestDTO personRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(personRequest.getEmail(), personRequest.getPassword()));

        String token = jwtService.generateToken(authentication.getName());
        Person person = personService.findByEmail(personRequest.getEmail());
        
        return createAuthResponse(token, person);
    }

    public AuthResponseDTO register(PersonRequestDTO personRequest) {
        Person person = new Person();
        person.setName(personRequest.getName());
        person.setEmail(personRequest.getEmail());
        person.setPassword(passwordEncoder.encode(personRequest.getPassword()));
        person.setValidationCode(UUID.randomUUID().toString());
        person.setActive(true);
        
        List<PersonProfile> personProfiles = new ArrayList<>();
        
        List<Long> profileIds = personRequest.getProfileIds();
        if (profileIds == null || profileIds.isEmpty()) {
            Profile defaultProfile = profileRepository.findByType(ProfileType.ROLE_BUYER)
                    .orElseThrow(() -> new RuntimeException("Perfil BUYER não encontrado"));
            profileIds = List.of(defaultProfile.getId());
        }
        
        for (Long profileId : profileIds) {
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new RuntimeException("Perfil com ID " + profileId + " não encontrado"));
            
            PersonProfile personProfile = new PersonProfile();
            personProfile.setProfile(profile);
            personProfile.setPerson(person);
            personProfiles.add(personProfile);
        }
        
        person.setPersonProfiles(personProfiles);
        
        Person savedPerson = personService.create(person);
        String token = jwtService.generateToken(savedPerson.getEmail());
        
        return createAuthResponse(token, savedPerson);
    }
    
    private AuthResponseDTO createAuthResponse(String token, Person person) {
        AuthResponseDTO.UserInfoDTO userInfo = new AuthResponseDTO.UserInfoDTO();
        userInfo.setId(person.getId());
        userInfo.setName(person.getName());
        userInfo.setEmail(person.getEmail());
        userInfo.setActive(person.isActive());
        userInfo.setProfileImage(person.getProfileImage());
        userInfo.setCreatedAt(person.getCreatedAt());
        
        List<AuthResponseDTO.UserInfoDTO.ProfileInfoDTO> profiles = person.getPersonProfiles()
                .stream()
                .map(pp -> new AuthResponseDTO.UserInfoDTO.ProfileInfoDTO(
                        pp.getProfile().getId(),
                        pp.getProfile().getType().name()
                ))
                .collect(Collectors.toList());
        
        userInfo.setProfiles(profiles);
        
        long expiresIn = jwtService.getExpirationTimeInSeconds();
        return new AuthResponseDTO(token, userInfo, expiresIn);
    }


    public AuthResponseDTO refreshToken(String email) {
        String newToken = jwtService.generateToken(email);
        
        Person person = personService.findByEmail(email);
        
        return createAuthResponse(newToken, person);
    }
}