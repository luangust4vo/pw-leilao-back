package com.github.luangust4vo.pw_leilao_backend.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.luangust4vo.pw_leilao_backend.models.Person;
import com.github.luangust4vo.pw_leilao_backend.models.Profile;
import com.github.luangust4vo.pw_leilao_backend.models.PersonProfile;
import com.github.luangust4vo.pw_leilao_backend.models.enums.ProfileType;
import com.github.luangust4vo.pw_leilao_backend.repositories.PersonRepository;
import com.github.luangust4vo.pw_leilao_backend.repositories.ProfileRepository;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired 
    private PasswordEncoder passwordEncoder;
    
    // Configurações do usuário ADMIN padrão
    @Value("${app.admin.name}")
    private String adminName;
    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        initializeProfiles();
        initializeAdminUser();
    }

    private void initializeProfiles() {
        for (ProfileType profileType : ProfileType.values()) {
            if (profileRepository.findByType(profileType).isEmpty()) {
                Profile profile = new Profile();
                profile.setType(profileType);
                profileRepository.save(profile);
            }
        }
    }

    private void initializeAdminUser() {
        if (personRepository.findByEmail(this.adminEmail).isEmpty()) {
            Profile adminProfile = profileRepository.findByType(ProfileType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Perfil ADMIN não encontrado"));
            
            Person admin = new Person();
            admin.setName("ADMIN");
            admin.setEmail(this.adminEmail);
            admin.setPassword(passwordEncoder.encode(this.adminPassword));
            admin.setActive(true);
            
            PersonProfile personProfile = new PersonProfile();
            personProfile.setProfile(adminProfile);
            personProfile.setPerson(admin);
            
            List<PersonProfile> personProfiles = new ArrayList<>();
            personProfiles.add(personProfile);
            admin.setPersonProfiles(personProfiles);
            
            personRepository.save(admin);
        }
    }
}
