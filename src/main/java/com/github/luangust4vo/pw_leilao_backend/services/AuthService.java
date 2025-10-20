package com.github.luangust4vo.pw_leilao_backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.context.Context;

import com.github.luangust4vo.pw_leilao_backend.dto.AuthResponseDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.PasswordResetRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.PersonRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.dto.UpdatePasswordRequestDTO;
import com.github.luangust4vo.pw_leilao_backend.exception.BusinessException;
import com.github.luangust4vo.pw_leilao_backend.exception.NotFoundException;
import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.PersonProfile;
import com.github.luangust4vo.pw_leilao_backend.models.Profile;
import com.github.luangust4vo.pw_leilao_backend.models.enums.ProfileType;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;
import com.github.luangust4vo.pw_leilao_backend.repositories.ProfileRepository;
import com.github.luangust4vo.pw_leilao_backend.utils.Const;

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
    private PersonRepository personRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponseDTO auth(PersonRequestDTO personRequest) {
        Person person = personService.findByEmail(personRequest.getEmail());

        if (!person.isActive()) {
            throw new BusinessException("Sua conta não está ativa. Por favor, verifique seu e-mail para ativá-la.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(personRequest.getEmail(), personRequest.getPassword()));

        var userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        
        return createAuthResponse(token, person);
    }

    @ResponseStatus(HttpStatus.OK)
    public void register(PersonRequestDTO personRequest) {
        if (personRepository.findByEmail(personRequest.getEmail()).isPresent()) {
            throw new BusinessException("Este e-mail já está cadastrado.");
        }

        Person person = new Person();
        person.setName(personRequest.getName());
        person.setEmail(personRequest.getEmail());
        person.setPassword(passwordEncoder.encode(personRequest.getPassword()));

        person.setActive(false);
        person.setValidationCode(UUID.randomUUID().toString());
        person.setValidationCodeExpiration(LocalDateTime.now().plusHours(24));
        
        List<PersonProfile> personProfiles = new ArrayList<>();
        List<Long> profileIds = personRequest.getProfileIds();
        if (profileIds == null || profileIds.isEmpty()) {
            Profile defaultProfile = profileRepository.findByType(ProfileType.ROLE_BUYER.name())
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
        sendVerificationEmail(savedPerson);
    }

    public AuthResponseDTO refreshToken(String email) {
        Person person = personService.findByEmail(email);
        
        String newToken = jwtService.generateToken(person);
        
        return createAuthResponse(newToken, person);
    }

    public AuthResponseDTO verifyAccount(String validationCode) {
        Person person = personRepository.findByValidationCode(validationCode)
                .orElseThrow(() -> new NotFoundException("Código de verificação inválido ou não encontrado."));

        if (person.isActive()) {
            throw new BusinessException("Esta conta já foi ativada.");
        }

        if (person.getValidationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Seu código de verificação expirou.");
        }

        person.setActive(true);
        person.setValidationCode(null);
        person.setValidationCodeExpiration(null);
        personRepository.save(person);

        String token = jwtService.generateToken(person);
        return createAuthResponse(token, person);
    }

    public void requestPasswordReset(PasswordResetRequestDTO request) {
        personRepository.findByEmail(request.getEmail()).ifPresent(person -> {
            person.setValidationCode(UUID.randomUUID().toString());
            person.setValidationCodeExpiration(LocalDateTime.now().plusMinutes(15));
            personRepository.save(person);
            sendPasswordResetEmail(person);
        });
    }

    public void verifyPasswordResetCode(String code) {
        Person person = personRepository.findByValidationCode(code)
                .orElseThrow(() -> new BusinessException("Código de verificação inválido ou expirado."));
        
        if (person.getValidationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Código de verificação inválido ou expirado.");
        }
    }

    public void updatePassword(UpdatePasswordRequestDTO request) {
        verifyPasswordResetCode(request.getCode());

        Person person = personRepository.findByValidationCode(request.getCode()).get();

        person.setPassword(passwordEncoder.encode(request.getNewPassword()));
        person.setValidationCode(null);
        person.setValidationCodeExpiration(null);
        personRepository.save(person);
    }

    private void sendPasswordResetEmail(Person person) {
        String verificationLink = "http://localhost:5173/verify-reset-code?code=" + person.getValidationCode();

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("name", person.getName());
        context.setVariable("verificationLink", verificationLink);
        
        emailService.emailTemplate(
            person.getEmail(), 
            "Redefinição de Senha", 
            "passwordResetEmail", // Novo template
            context
        );
    }

    private void sendVerificationEmail(Person person) {
        String verificationLink = "http://localhost:5173/verify-account?code=" + person.getValidationCode();

        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("name", person.getName());
        context.setVariable("verificationLink", verificationLink);
        
        emailService.emailTemplate(
            person.getEmail(), 
            "Ative sua Conta - Leilão Online", 
            Const.templateSuccessRegister,
            context
        );
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
                        pp.getProfile().getType()
                ))
                .collect(Collectors.toList());
        
        userInfo.setProfiles(profiles);
        
        long expiresIn = jwtService.getExpirationTimeInSeconds();
        return new AuthResponseDTO(token, userInfo, expiresIn);
    }
}